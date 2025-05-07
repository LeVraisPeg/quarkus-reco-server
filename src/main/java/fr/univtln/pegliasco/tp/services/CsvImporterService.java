    package fr.univtln.pegliasco.tp.services;

    import fr.univtln.pegliasco.tp.model.*;
    import jakarta.enterprise.context.ApplicationScoped;
    import jakarta.inject.Inject;
    import jakarta.transaction.Transactional;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    import com.opencsv.CSVReader;
    import java.io.FileReader;

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

        @Transactional
        public void importRatingsFromCsv(String filePath) throws IOException, com.opencsv.exceptions.CsvValidationException {
            final int batchSize = 5000;
            List<Rating> batch = new ArrayList<>();

            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
                String[] tokens;

                // Ignore the header
                csvReader.readNext();

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 4) {
                        Long userId = Long.parseLong(tokens[0]);
                        Long movieId = Long.parseLong(tokens[1]);
                        Float rate = Float.parseFloat(tokens[2]);

                        Rating rating = new Rating();


                        Movie movie = movieService.getMovieById(movieId);
                        if (movie != null) {
                            rating.setMovie(movie);


                            if (movie.getRatings() == null) {
                                movie.setRatings(new ArrayList<>());
                            }
                            movie.getRatings().add(rating);
                        } else {
                            System.out.println("Movie not found for movieId: " + movieId);
                            continue;
                        }


                        Account account = accountService.getAccountById(userId);
                        if (account != null) {
                            rating.setAccount(account);
                        } else {
                            Account newAccount = new Account();
                            newAccount.setId(userId);
                        }


                        rating.setRate(rate);

                        // Add the rating to the batch
                        batch.add(rating);


                        if (batch.size() >= batchSize) {
                            persistRatingBatch(batch);
                            batch.clear();
                        }
                    }
                }


                if (!batch.isEmpty()) {
                    persistRatingBatch(batch);
                }

                System.out.println("Imported ratings from: " + filePath);
            }
        }


        @Transactional
        public void persistRatingBatch(List<Rating> ratings) {
            for (Rating rating : ratings) {
                ratingService.saveOrUpdate(rating);
                System.out.println("Persisted rating: " + rating.getRate());
            }
        }

        public void importMoviesFromCsv(String filePath) throws IOException, com.opencsv.exceptions.CsvValidationException {
            final int batchSize = 5000;
            List<Movie> batch = new ArrayList<>();

            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
                String[] tokens;

                // Ignore the header
                csvReader.readNext();

                while ((tokens = csvReader.readNext()) != null) {
                    if (tokens.length >= 3) {
                        Long movieId = Long.parseLong(tokens[0]);
                        Movie movie = movieService.getMovieById(movieId);

                        if (movie == null) {
                            movie = new Movie();
                        }

                        movie.setTitle(tokens[1].trim());

                        String[] genreNames = tokens[2].split("\\|");
                        List<Gender> genderList = new ArrayList<>();
                        for (String genreName : genreNames) {
                            Gender gender = genderService.findOrCreateByName(genreName.trim());
                            genderList.add(gender);
                        }

                        movie.setGenders(genderList);
                        batch.add(movie);

                        if (batch.size() >= batchSize) {
                            PersistMovieBatch(batch);
                            batch.clear();
                        }
                    }
                }

                if (!batch.isEmpty()) {
                    PersistMovieBatch(batch);
                }

                System.out.println("Imported movies from: " + filePath);
            }
        }

        @Transactional
        public void PersistMovieBatch(List<Movie> movies) {
            for (Movie movie : movies) {
                movieService.saveOrUpdate(movie);
                System.out.println("Persisted movie: " + movie.getTitle());
            }
        }






    }
