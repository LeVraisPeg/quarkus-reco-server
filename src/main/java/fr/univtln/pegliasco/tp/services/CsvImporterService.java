package fr.univtln.pegliasco.tp;

import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.User;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.services.RatingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

@ApplicationScoped
public class CsvImporterService {

    @Inject
    RatingService ratingService;

    public void importRatingsFromFolder(String folderPath) {
        try (Stream<Path> paths = Files.list(Paths.get(folderPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .forEach(this::processCsvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processCsvFile(Path csvFilePath) {
        try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    Rating rating = new Rating();
                    User user = new User(); // Assuming you have a way to fetch or create a User
                    user.setId(Long.parseLong(tokens[0])); // Assuming the first column is user ID
                    rating.setUser(user);

                    Movie movie = new Movie(); // Assuming you have a way to fetch or create a Movie
                    movie.setId(Long.parseLong(tokens[1])); // Assuming the second column is movie ID
                    rating.setMovie(movie);

                    rating.setRate(Integer.parseInt(tokens[2])); // Assuming the third column is the rating

                    ratingService.addRating(rating);
                }
            }

            System.out.println("Processed file: " + csvFilePath.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}