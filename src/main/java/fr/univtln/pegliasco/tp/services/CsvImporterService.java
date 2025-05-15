    package fr.univtln.pegliasco.tp.services;

    import com.opencsv.exceptions.CsvValidationException;
    import fr.univtln.pegliasco.tp.model.*;
    import jakarta.enterprise.context.ApplicationScoped;
    import jakarta.inject.Inject;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.EntityManagerFactory;
    import jakarta.persistence.EntityTransaction;

    import java.io.IOException;
    import java.util.*;
    import java.util.concurrent.CompletableFuture;

    import com.opencsv.CSVReader;
    import java.io.FileReader;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.logging.Logger;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
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

        private static final Logger logger = Logger.getLogger(CsvImporterService.class.getName());
        @Inject
        TagService tagService;


        public void importRatingsFromCsv(String filePath) throws IOException, CsvValidationException {
            final int batchSize = 5000;
            List<Rating> currentBatch = new ArrayList<>(batchSize);

            Map<Long, Account> accountCache = accountService.findAllAsMap();
            Map<Long, Movie> movieCache = movieService.findAllAsMap();

            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
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

                System.out.println("Import terminé depuis : " + filePath);
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

        public void importMoviesFromCsv(String filePath) throws IOException, CsvValidationException {
            final int batchSize = 5000;
            List<Movie> allMovies = new ArrayList<>();
            Map<String, Gender> genreCache = new HashMap<>();

            // Lecture du CSV et préparation des données
            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
                String[] tokens;
                csvReader.readNext(); // Skip header

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 3) {
                        Movie movie = new Movie();
                        movie.setId(Long.parseLong(tokens[0]));
                        String rawTitle = tokens[1].trim();

                        // Extraire l'année
                        Pattern pattern = Pattern.compile("\\((\\d{4})\\)");
                        Matcher matcher = pattern.matcher(rawTitle);
                        if (matcher.find()) {
                            int year = Integer.parseInt(matcher.group(1));
                            movie.setYear(year);
                        } else {
                            movie.setYear(0); // ou -1
                        }

                        String cleanTitle = rawTitle.replaceAll("\\s*\\(\\d{4}\\)", "").trim();

                        // Corriger les titres du type "Title, The" → "The Title"
                        Pattern articlePattern = Pattern.compile("^(.*),\\s*(The|A|An)$", Pattern.CASE_INSENSITIVE);
                        Matcher articleMatcher = articlePattern.matcher(cleanTitle);
                        if (articleMatcher.find()) {
                            cleanTitle = articleMatcher.group(2) + " " + articleMatcher.group(1);
                        }

                        movie.setTitle(cleanTitle);


                        String[] genreNames = tokens[2].split("\\|");
                        List<Gender> genderList = new ArrayList<>();
                        for (String genreName : genreNames) {
                            genreName = genreName.trim();
                            Gender gender = genreCache.computeIfAbsent(genreName, name -> genderService.findOrCreateByName(name));
                            genderList.add(gender);
                        }

                        movie.setGenders(genderList);
                        allMovies.add(movie);
                    }
                }
            }

            // Diviser les films en lots pour le parallélisme
            List<List<Movie>> batches = new ArrayList<>();
            for (int i = 0; i < allMovies.size(); i += batchSize) {
                batches.add(allMovies.subList(i, Math.min(i + batchSize, allMovies.size())));
            }

            // Exécution parallèle des batches avec gestion explicite des transactions
            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> persistBatchWithTransaction(batch)))
                    .collect(Collectors.toList());

            // Attendre que tous les futures soient terminés
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            System.out.println("Imported movies from: " + filePath);
        }



        private void persistBatchWithTransaction(List<Movie> movies) {
            // Créer un nouveau EntityManager pour chaque thread
            EntityManager entityManager = em.getEntityManagerFactory().createEntityManager();

            // Démarrer la transaction manuellement dans chaque thread
            entityManager.getTransaction().begin();

            try {
                for (int i = 0; i < movies.size(); i++) {
                    entityManager.persist(movies.get(i));
                    if (i % 1000 == 0) {  // Flush tous les 1000 éléments
                        entityManager.flush();
                    }
                }
                entityManager.flush();  // Flush final
                entityManager.getTransaction().commit();  // Commit la transaction

            } catch (Exception e) {
                // En cas d'erreur, annuler la transaction
                entityManager.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                entityManager.close();  // Fermer l'EntityManager
            }
        }


        public void importTagsFromCsv(String filePath) throws IOException, CsvValidationException {
            final int batchSize = 5000;
            List<Tag> currentBatch = new ArrayList<>(batchSize);

            // Mise en cache des comptes et films
            Map<Long, Account> accountCache = accountService.findAllAsMap();
            Map<Long, Movie> movieCache = movieService.findAllAsMap();

            // Map pour éviter les doublons de tags : "tagName::userId"
            Map<String, Tag> tagMap = new HashMap<>();

            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
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
                System.out.println("Import terminé depuis : " + filePath);
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
