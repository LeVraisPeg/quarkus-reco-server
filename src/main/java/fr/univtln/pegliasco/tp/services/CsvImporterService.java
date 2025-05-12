    package fr.univtln.pegliasco.tp.services;

    import com.opencsv.exceptions.CsvValidationException;
    import fr.univtln.pegliasco.tp.model.*;
    import jakarta.enterprise.context.ApplicationScoped;
    import jakarta.inject.Inject;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.EntityTransaction;
    import jakarta.persistence.PersistenceContext;
    import jakarta.transaction.Transactional;


    import java.io.IOException;
    import java.util.*;
    import java.util.concurrent.CompletableFuture;

    import com.opencsv.CSVReader;
    import java.io.FileReader;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.function.Function;
    import java.util.stream.Collectors;

    @ApplicationScoped
    public class CsvImporterService {

        @Inject
        RatingService ratingService;

        @Inject
        MovieService movieService;

        @Inject
        GenderService genderService;
        @Inject
        AccountService accountService;
        @Inject
        TagService tagService;
        @Inject
        EntityManager em;

        public void importRatingsFromCsv(String filePath) throws IOException, CsvValidationException {
            final int batchSize = 5000;
            List<Rating> allRatings = new ArrayList<>();

            // Préchargement des comptes et films existants
            Map<Long, Account> accountCache = accountService.findAllAsMap();
            Map<Long, Movie> movieCache = movieService.findAllAsMap();

            // Lecture du CSV
            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
                String[] tokens;
                csvReader.readNext(); // skip header

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 4) {
                        Long userId = Long.parseLong(tokens[0]);
                        Long movieId = Long.parseLong(tokens[1]);
                        float ratingValue = Float.parseFloat(tokens[2]);

                        // Récupération du compte (ou création si nécessaire)
                        Account account = accountCache.computeIfAbsent(userId, id -> accountService.findOrCreateById(id));


                        // Récupération du film (ou null si inconnu)
                        Movie movie = movieCache.get(movieId);
                        if (movie == null) continue; // ignorer les ratings sans film

                        // Création du rating
                        Rating rating = new Rating();
                        rating.setRate(ratingValue);
                        rating.setAccount(account);
                        rating.setMovie(movie);

                        allRatings.add(rating);
                    }
                }
            }

            // Batch persist (inchangé)
            List<List<Rating>> batches = new ArrayList<>();
            for (int i = 0; i < allRatings.size(); i += batchSize) {
                batches.add(allRatings.subList(i, Math.min(i + batchSize, allRatings.size())));
            }

            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> persistBatchRatingWithTransaction(batch), executor))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();

            System.out.println("Imported ratings from: " + filePath);
        }



        private void persistBatchRatingWithTransaction(List<Rating> ratings) {
            // Create a new EntityManager for each thread
            EntityManager entityManager = em.getEntityManagerFactory().createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();

            try {
                // Start the transaction manually
                transaction.begin();

                for (int i = 0; i < ratings.size(); i++) {
                    Rating rating = ratings.get(i);

                    // Use merge() to reattach detached entities
                    Account managedAccount = entityManager.merge(rating.getAccount());
                    Movie managedMovie = entityManager.merge(rating.getMovie());

                    // Assign the managed entities
                    rating.setAccount(managedAccount);
                    rating.setMovie(managedMovie);

                    // Persist the Rating entity
                    entityManager.persist(rating);

                    if (i % 1000 == 0) {  // Flush every 1000 elements
                        entityManager.flush();
                        entityManager.clear();  // Clear the persistence context to free memory
                    }
                }

                entityManager.flush();  // Final flush
                transaction.commit();  // Commit the transaction

            } catch (Exception e) {
                // Rollback the transaction in case of an error
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                e.printStackTrace();
            } finally {
                entityManager.close();  // Close the EntityManager
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
                        movie.setTitle(tokens[1].trim());

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



        //importTagsFromCsv
        @Transactional
        public void importTagsFromCsv(String filePath) throws IOException, CsvValidationException {
            final int batchSize = 5000;
            List<Tag> batch = new ArrayList<>();

            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
                String[] tokens;

                // Ignore header
                csvReader.readNext();

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 4) {
                        Long userId = Long.parseLong(tokens[0]);
                        Long movieId = Long.parseLong(tokens[1]);
                        String tagName = tokens[2].trim();
                        long timestamp = Long.parseLong(tokens[3]);

                        Movie movie = movieService.getMovieById(movieId);
                        if (movie == null) {
                            System.out.println("Movie not found for movieId: " + movieId);
                            continue;
                        }

                        Account account = accountService.getAccountById(userId);
                        if (account == null) {
                            Account newAccount = new Account();
                            newAccount.setId(userId);
                        }

                       //new tag
                        Tag tag = new Tag();
                        tag.setName(tagName);
                        tag.setAccount(account);
                        tag.setMovies(new ArrayList<>());


                        // Add movie if not already associated
                        if (!tag.getMovies().contains(movie)) {
                            tag.getMovies().add(movie);
                        }

                        // Add tag to movie if not already present
                        if (!movie.getTags().contains(tag)) {
                            movie.getTags().add(tag);
                        }

                        batch.add(tag);

                        if (batch.size() >= batchSize) {
                            persistTagBatch(batch);
                            batch.clear();
                        }
                    }
                }

                if (!batch.isEmpty()) {
                    persistTagBatch(batch);
                }

                System.out.println("Imported tags from: " + filePath);
            }
        }

        @Transactional
        public void persistTagBatch(List<Tag> tags) {
            for (Tag tag : tags) {
                tagService.saveOrUpdate(tag);
                System.out.println("Persisted tag: " + tag.getName());
            }
        }






    }
