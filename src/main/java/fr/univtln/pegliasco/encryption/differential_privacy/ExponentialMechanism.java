package fr.univtln.pegliasco.encryption.differential_privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;

import fr.univtln.pegliasco.tp.model.Movie;

/**
 * The {@code ExponentialMechanism} class implements the Exponential Mechanism
 * for differential privacy. It is used to select an item (e.g., a movie)
 * from a list based on a probability distribution derived from utility scores.
 */
public class ExponentialMechanism {
    private static final int MAX_RATING = 5;
    private static final double SENSITIVITY = 0.5;
    private final Random randomGenerator = new Random();

    /**
     * Computes the utility of a given rating.
     *
     * @param rating the rating to evaluate
     * @return the utility value of the rating
     */
    private double computeUtility(double rating) {
        return -Math.abs(rating - MAX_RATING);
    }

    /**
     * Generates a cumulative probability distribution based on the predicted
     * ratings and the privacy parameter epsilon.
     *
     * @param predictedRatings the list of predicted ratings
     * @param epsilon          the privacy parameter
     * @return a list of cumulative probabilities
     * @throws IllegalArgumentException if the sum of generated values is zero
     */
    private List<Double> generateCumulativeProbabilities(List<Double> predictedRatings, double epsilon) {
        if (predictedRatings == null || predictedRatings.isEmpty()) {
            throw new IllegalArgumentException("The predicted ratings list cannot be null or empty.");
        }

        List<Double> cumulativeProbabilities = new ArrayList<>();
        double sumOfValues = 0;

        for (double rating : predictedRatings) {
            double value = Math.exp(epsilon * computeUtility(rating) / (2 * SENSITIVITY));
            sumOfValues += value;
            cumulativeProbabilities.add(value);
        }

        if (sumOfValues == 0) {
            throw new IllegalArgumentException(
                    "Sum of generated values is zero, cannot normalize to generate probabilities.");
        }

        for (int i = 0; i < cumulativeProbabilities.size(); i++) {
            if (i == 0) {
                cumulativeProbabilities.set(i, cumulativeProbabilities.get(i) / sumOfValues);
            } else {
                cumulativeProbabilities.set(i,
                        cumulativeProbabilities.get(i) / sumOfValues + cumulativeProbabilities.get(i - 1));
            }
        }
        return cumulativeProbabilities;
    }

    /**
     * Generates a pair containing a list of movies and their corresponding
     * predicted ratings.
     *
     * @param recommendedMovies a map of movies and their associated scores
     * @return a pair containing the list of movies and the list of predicted
     *         ratings
     */
    private Pair<List<Movie>, List<Double>> generateMoviesAndRatings(Map<Movie, Double> recommendedMovies) {
        List<Movie> movieList = new ArrayList<>();
        List<Double> predictedRatings = new ArrayList<>();

        for (Map.Entry<Movie, Double> entry : recommendedMovies.entrySet()) {
            movieList.add(entry.getKey());
            predictedRatings.add(entry.getValue());
        }

        Pair<List<Movie>, List<Double>> pair = new Pair<>();
        pair.setFirst(movieList);
        pair.setSecond(predictedRatings);
        return pair;
    }

    /**
     * Finds the index of the first element in the cumulative probability list
     * that is greater than or equal to the given random value.
     *
     * @param cumulativeProbabilities the list of cumulative probabilities
     * @param randomValue             the random value to compare
     * @return an {@code OptionalInt} containing the index if found, or empty if not
     *         found
     */
    private OptionalInt findIndexWithRandomValue(List<Double> cumulativeProbabilities, double randomValue) {
        for (int index = 0; index < cumulativeProbabilities.size(); index++) {
            if (randomValue < cumulativeProbabilities.get(index)) {
                return OptionalInt.of(index);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Selects a random movie from the recommended movie list based on the
     * Exponential Mechanism.
     *
     * @param recommendedMovies a map of movies and their associated scores
     * @param epsilon           the privacy parameter
     * @return the selected movie
     * @throws IllegalStateException if no movie is found for the given random value
     */
    public Movie selectRandomMovie(Map<Movie, Double> recommendedMovies, double epsilon) {
        if (recommendedMovies == null || recommendedMovies.isEmpty()) {
            throw new IllegalArgumentException("The recommended movies map cannot be null or empty.");
        }

        double randomValue = randomGenerator.nextDouble();

        Pair<List<Movie>, List<Double>> pair = generateMoviesAndRatings(recommendedMovies);

        List<Double> cumulativeProbabilities = generateCumulativeProbabilities(pair.getSecond(), epsilon);
        OptionalInt index = findIndexWithRandomValue(cumulativeProbabilities, randomValue);

        if (index.isPresent()) {
            return pair.getFirst().get(index.getAsInt());
        } else {
            throw new IllegalStateException("No movie found for the given random value.");
        }
    }

    public static void main(String[] args) {
        // Example usage

        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Inception");
        movie1.setYear(2010);

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("The Matrix");
        movie2.setYear(1999);

        Movie movie3 = new Movie();
        movie3.setId(3L);
        movie3.setTitle("Interstellar");
        movie3.setYear(2014);

        Movie movie4 = new Movie();
        movie4.setId(4L);
        movie4.setTitle("The Dark Knight");
        movie4.setYear(2008);

        Map<Movie, Double> recommendedMovies = Map.of(
                movie1, 3.0,
                movie2, 4.0,
                movie3, 1.5,
                movie4, 4.9);

        ExponentialMechanism mechanism = new ExponentialMechanism();
        Movie selectedMovie = mechanism.selectRandomMovie(recommendedMovies, 0.5);
        System.out.println("Selected movie: " + selectedMovie.getTitle() + " (" + selectedMovie.getYear() + ")");
    }
}