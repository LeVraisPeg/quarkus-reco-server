package fr.univtln.pegliasco.encryption.differential_privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

import fr.univtln.pegliasco.tp.movies_to_recommend.RecommendedMovie;

/**
 * The {@code ExponentialMechanism} class implements the Exponential Mechanism
 * for differential privacy. It is used to select items (e.g., movies) from a
 * list based on a probability distribution derived from utility scores.
 * 
 * <p>
 * The Exponential Mechanism ensures that the selection process adheres to
 * differential privacy principles by assigning probabilities to items based on
 * their utility scores and a privacy parameter {@code epsilon}.
 * </p>
 */
public class ExponentialMechanism {

    private ExponentialMechanism() {
    }

    private static final int MAX_RATING = 5;
    private static final double SENSITIVITY = 0.5;
    private static final Random randomGenerator = new Random();

    /**
     * Computes the utility of a given rating.
     * 
     * <p>
     * The utility is calculated as the negative absolute difference between the
     * rating and the maximum possible rating. Higher ratings have higher utility.
     * </p>
     *
     * @param rating the rating to evaluate
     * @return the utility value of the rating
     */
    private static double computeUtility(double rating) {
        return -Math.abs(rating - MAX_RATING);
    }

    /**
     * Generates a cumulative probability distribution based on the predicted
     * ratings and the privacy parameter {@code epsilon}.
     * 
     * <p>
     * The probabilities are calculated using the exponential function, which
     * ensures that higher utility scores have higher probabilities. The
     * probabilities are normalized to form a cumulative distribution.
     * </p>
     *
     * @param recommendedMovies the list of recommended movies with their ratings
     * @param epsilon           the privacy parameter, controlling the level of
     *                          randomness
     * @return a list of cumulative probabilities
     * @throws IllegalArgumentException if the input list is null, empty, or if the
     *                                  sum of generated values is zero
     */
    private static List<Double> generateCumulativeProbabilities(List<RecommendedMovie> recommendedMovies,
            double epsilon) {
        if (recommendedMovies == null || recommendedMovies.isEmpty()) {
            throw new IllegalArgumentException("The predicted ratings list cannot be null or empty.");
        }

        List<Double> cumulativeProbabilities = new ArrayList<>();
        double sumOfValues = 0;

        // Calculate exponential values and their sum
        for (RecommendedMovie recommendation : recommendedMovies) {
            double value = Math.exp(epsilon * computeUtility(recommendation.getRate()) / (2 * SENSITIVITY));
            sumOfValues += value;
            cumulativeProbabilities.add(value);
        }

        if (sumOfValues == 0) {
            throw new IllegalArgumentException(
                    "Sum of generated values is zero, cannot normalize to generate probabilities.");
        }

        // Normalize and compute cumulative probabilities
        for (int i = 0; i < cumulativeProbabilities.size(); i++) {
            double normalizedValue = cumulativeProbabilities.get(i) / sumOfValues;
            if (i == 0) {
                cumulativeProbabilities.set(i, normalizedValue);
            } else {
                cumulativeProbabilities.set(i, normalizedValue + cumulativeProbabilities.get(i - 1));
            }
        }
        return cumulativeProbabilities;
    }

    /**
     * Finds the index of the first element in the cumulative probability list
     * that is greater than or equal to the given random value.
     *
     * @param cumulativeProbabilities the list of cumulative probabilities
     * @param randomValue             the random value to compare
     * @return an {@code OptionalInt} containing the index if found, or empty if
     *         not found
     */
    private static OptionalInt findIndexWithRandomValue(List<Double> cumulativeProbabilities, double randomValue) {
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
     * <p>
     * The selection is performed by generating a cumulative probability
     * distribution and using a random value to select an item. The probability of
     * selecting a movie is proportional to its utility score.
     * </p>
     *
     * @param recommendedMovies the list of recommended movies with their ratings
     * @param epsilon           the privacy parameter, controlling the level of
     *                          randomness
     * @return the selected movie
     * @throws IllegalArgumentException if the input list is null or empty
     * @throws IllegalStateException    if no movie is found for the given random
     *                                  value
     */
    public static RecommendedMovie selectRandomMovie(List<RecommendedMovie> recommendedMovies, double epsilon) {
        if (recommendedMovies == null || recommendedMovies.isEmpty()) {
            throw new IllegalArgumentException(
                    "The recommended movies list cannot be null or empty, and the number of movies must be positive and less than or equal to the size of the list.");
        }

        double randomValue = randomGenerator.nextDouble();
        List<Double> cumulativeProbabilities = generateCumulativeProbabilities(recommendedMovies, epsilon);
        OptionalInt index = findIndexWithRandomValue(cumulativeProbabilities, randomValue);

        if (index.isPresent()) {
            return recommendedMovies.get(index.getAsInt());
        } else {
            throw new IllegalStateException("No movie found for the given random value.");
        }
    }
}