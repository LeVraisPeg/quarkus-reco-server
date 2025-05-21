    package fr.univtln.pegliasco.tp.services;

    import com.opencsv.CSVParserBuilder;
    import com.opencsv.CSVReaderBuilder;
    import com.opencsv.exceptions.CsvValidationException;
    import fr.univtln.pegliasco.tp.model.*;
    import fr.univtln.pegliasco.tp.model.nosql.MovieElastic;
    import fr.univtln.pegliasco.tp.model.nosql.MovieMapper;
    import jakarta.enterprise.context.ApplicationScoped;
    import jakarta.inject.Inject;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.EntityManagerFactory;
    import jakarta.persistence.EntityTransaction;

    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.*;
    import java.util.concurrent.CompletableFuture;

    import org.jboss.logging.Logger;
    import com.opencsv.CSVReader;

    import java.util.stream.Collectors;

    @ApplicationScoped
    public class CsvImporterService {


        @Inject
        MovieService movieService;
        @Inject
        GenderService genderService;
        @Inject
        AccountService accountService;
        @Inject
        EntityManager em;
        @Inject
        EntityManagerFactory entityManagerFactory;

        @Inject
        MovieElasticService movieElasticService;

        private static final Logger logger = Logger.getLogger(CsvImporterService.class.getName());
        @Inject
        TagService tagService;

        private void waitForElasticsearchReady() {
            int maxRetries = 30;
            int retry = 0;
            while (retry < maxRetries) {
                try {
                    movieElasticService.ping(); // À implémenter dans MovieElasticService
                    logger.info("Elasticsearch est prêt.");
                    return;
                } catch (Exception e) {
                    retry++;
                    logger.warnf("Elasticsearch non prêt, tentative %d/%d", retry, maxRetries);
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                }
            }
            throw new RuntimeException("Elasticsearch n'est pas prêt après plusieurs tentatives.");
        }

        public void importRatingsFromCsv(InputStream inputStream) throws IOException, CsvValidationException {
            final int batchSize = 10000;
            List<Rating> currentBatch = new ArrayList<>(batchSize);

            Map<Long, Account> accountCache = accountService.findAllAsMap();
            Map<Long, Movie> movieCache = movieService.findAllAsMap();

            try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
                String[] tokens;
                csvReader.readNext(); // skip header


                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 4) {
                        Long userId = Long.parseLong(tokens[0]);
                        Long movieId = Long.parseLong(tokens[1]);
                        float ratingValue = Float.parseFloat(tokens[2]);

                        Account account = accountCache.computeIfAbsent(userId, id -> accountService.findOrCreateById(id));
                        if (account == null) {
                            logger.info("Account not found for userId: " + userId);
                            continue;
                        };
                        Movie movie = movieCache.get(movieId);
                        if (movie == null) continue;

                        Rating rating = new Rating();
                        rating.setRate(ratingValue);
                        rating.setAccount(account);
                        rating.setMovie(movie);
                        currentBatch.add(rating);

                        if (currentBatch.size() == batchSize) {
                            persistBatchRating(currentBatch);
                            currentBatch.clear();
                        }
                    }
                }

                // Dernier batch
                if (!currentBatch.isEmpty()) {
                    persistBatchRating(currentBatch);
                }

                System.out.println("Import terminé depuis : " + inputStream.toString());
            }
        }



        private void persistBatchRating(List<Rating> ratings) {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                for (int i = 0; i < ratings.size(); i++) {
                    Rating r = ratings.get(i);
                    em.persist(r);

                    if (i % 1000 == 0) {
                        em.flush();
                        em.clear();
                    }
                }

                em.flush();
                em.clear();
                tx.commit();

            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }

        private Date parseDateSafely(String dateStr) {
            if (dateStr == null || dateStr.trim().equalsIgnoreCase("Unknown")) {
                return null;
            }

            List<String> formats = List.of(
                    "dd MMM yyyy",      // Ex: 22 Nov 1995
                    "yyyy-MM-dd",       // Ex: 1995-12-29
                    "yyyy/MM/dd"        // Ajoute ici d'autres formats si besoin
            );

            for (String format : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                    sdf.setLenient(false);
                    return sdf.parse(dateStr);
                } catch (ParseException ignored) {
                }
            }

            // Log si aucun format ne correspond
            //System.out.println("Erreur de parsing de la date : " + dateStr);
            return null;
        }



        public void importMoviesFromCsv(InputStream inputStream) throws IOException, CsvValidationException {
            final int batchSize = 10000;
            List<Movie> allMovies = new ArrayList<>();
            Map<String, Gender> genreCache = new HashMap<>();

            try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator(',')
                            .withQuoteChar('"')
                            .build())
                    .build()) {

                String[] tokens;
                csvReader.readNext(); // Skip header

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length < 11) {
                        //logger.warnf("Ligne ignorée (colonnes insuffisantes) : %s", Arrays.toString(tokens));
                        continue;
                    }

                    try {
                        Movie movie = new Movie();
                        movie.setId(Long.parseLong(tokens[0].trim()));
                        movie.setTitle(tokens[1].trim());
                        movie.setYear(parseDateSafely(tokens[2].trim()));
                        if (movie.getYear() == null) continue;

                        movie.setRuntime(Integer.parseInt(tokens[3].trim()));

                        String[] genreNames = tokens[4].split("\\|");
                        List<Gender> genderList = new ArrayList<>();
                        for (String genreName : genreNames) {
                            genreName = genreName.trim();
                            Gender gender = genreCache.computeIfAbsent(genreName, name -> genderService.findOrCreateByName(name));
                            genderList.add(gender);
                        }
                        movie.setGenders(genderList);

                        movie.setDirector(tokens[5].trim());

                        movie.setWriters(Arrays.stream(tokens[6].split("\\|"))
                                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
                        movie.setActors(Arrays.stream(tokens[7].split("\\|"))
                                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
                        movie.setPlot(tokens[8].trim());
                        movie.setCountry(tokens[9].trim());
                        movie.setPoster(tokens[10].trim());

                        allMovies.add(movie);

                    } catch (Exception e) {
                        logger.errorf(e, "Erreur lors du parsing de la ligne : %s", Arrays.toString(tokens));
                    }
                }
            }

            //logger.infof("Nombre total de films lus : %d", allMovies.size());

            // Diviser en batches
            List<List<Movie>> batches = new ArrayList<>();
            for (int i = 0; i < allMovies.size(); i += batchSize) {
                batches.add(allMovies.subList(i, Math.min(i + batchSize, allMovies.size())));
            }

            //logger.infof("Nombre total de batches à persister : %d", batches.size());

            // Persistance parallèle
            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> persistBatchWithTransaction(batch)))
                    .collect(Collectors.toList());

            // Attendre la fin
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            logger.infof("Importation terminée depuis le fichier : %s", inputStream.toString());
        }



        private void persistBatchWithTransaction(List<Movie> movies) {
            // Attendre qu'Elasticsearch soit prêt avant de commencer le batch
            waitForElasticsearchReady();

            EntityManager entityManager = em.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();

            try {
                for (int i = 0; i < movies.size(); i++) {
                    Movie movie = movies.get(i);
                    try {
                        entityManager.persist(movie);

                        // Retry sur l'indexation Elasticsearch
                        int maxRetries = 5;
                        int retry = 0;
                        boolean success = false;
                        while (retry < maxRetries && !success) {
                            try {
                                MovieElastic movieElastic = MovieMapper.toElastic(movie);
                                movieElasticService.indexMovie(movieElastic);
                                success = true;
                            } catch (IOException e) {
                                retry++;
                                if (retry >= maxRetries) {
                                    e.printStackTrace();
                                } else {
                                    Thread.sleep(2000); // attendre 2s avant de réessayer
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.errorf(e, "Erreur lors de la persistance du film : %s (ID=%d)", movie.getTitle(), movie.getId());
                    }
                    if (i % 1000 == 0) {
                        entityManager.flush();
                    }
                }
                entityManager.flush();
                entityManager.getTransaction().commit();

            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                logger.error("Erreur lors de la persistance batch", e);

            } finally {
                entityManager.close();
            }
        }


        public void importTagsFromCsv(InputStream inputStream) throws IOException, CsvValidationException {
            final int batchSize = 10000;
            List<Tag> currentBatch = new ArrayList<>(batchSize);

            // Mise en cache des comptes et films
            Map<Long, Account> accountCache = accountService.findAllAsMap();
            Map<Long, Movie> movieCache = movieService.findAllAsMap();

            // Map pour éviter les doublons de tags : "tagName::userId"
            Map<String, Tag> tagMap = new HashMap<>();

            try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
                String[] tokens;
                csvReader.readNext(); // skip header

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 3) {
                        Long userId = Long.parseLong(tokens[0]);
                        Long movieId = Long.parseLong(tokens[1]);
                        String tagName = tokens[2].trim().toLowerCase();

                        Account account = accountCache.get(userId);
                        if (account == null) continue;

                        Movie movie = movieCache.get(movieId);
                        if (movie == null) continue;

                        String tagKey = tagName + "::" + userId;
                        Tag tag = tagMap.computeIfAbsent(tagKey, k -> {
                            Tag t = new Tag();
                            t.setName(tagName);
                            t.setAccount(account);
                            return t;
                        });

                        if (tag.getMovies() == null) tag.setMovies(new ArrayList<>());
                        if (!tag.getMovies().contains(movie)) {
                            tag.getMovies().add(movie);
                        }

                        if (movie.getTags() == null) movie.setTags(new ArrayList<>());
                        if (!movie.getTags().contains(tag)) {
                            movie.getTags().add(tag);
                        }
                    }
                }

                currentBatch.addAll(tagMap.values());
                persistBatchTag(currentBatch);
                System.out.println("Import terminé depuis : " + inputStream.toString());
            }
        }



        private void persistBatchTag(List<Tag> tags) {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                for (int i = 0; i < tags.size(); i++) {
                    Tag tag = tags.get(i);

                    // Rendre les objets gérés
                    Account managedAccount = em.merge(tag.getAccount());
                    List<Movie> managedMovies = tag.getMovies().stream()
                            .map(em::merge)
                            .toList();

                    tag.setAccount(managedAccount);
                    tag.setMovies(managedMovies);

                    Tag managedTag = em.merge(tag); // merge obligatoire

                    // Mise à jour des films pour refléter la relation
                    for (Movie m : managedMovies) {
                        if (m.getTags() == null) {
                            m.setTags(new ArrayList<>());
                        }
                        if (!m.getTags().contains(managedTag)) {
                            m.getTags().add(managedTag);
                            em.merge(m);
                        }
                    }

                    if (i % 1000 == 0) {
                        em.flush();
                        em.clear();
                    }
                }

                em.flush();
                em.clear();
                tx.commit();

            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }









    }
