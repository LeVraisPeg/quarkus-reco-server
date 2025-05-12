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
    private static final int MAX_NOTATION = 5;
    private static final double SENSIBILITY = 0.5;
    private final Random rdGenerator = new Random();

    /**
     * Computes the utility of a given note.
     *
     * @param note the note to evaluate
     * @return the utility value of the note
     */
    private double getUtility(double note) {
        return -Math.abs(note - MAX_NOTATION);
    }

    /**
     * Generates a cumulative probability distribution based on the predicted
     * notations and the privacy parameter epsilon.
     *
     * @param predectedEvaluation the list of predicted notations
     * @param epsilon             the privacy parameter
     * @return a list of cumulative probabilities
     * @throws IllegalArgumentException if the sum of generated values is zero
     */
    private List<Double> generateProbability(List<Double> predectedEvaluation, double epsilon) {
        List<Double> cumulateProbability = new ArrayList<>();
        double sumOfGeneratedValues = 0;
        for (double note : predectedEvaluation) {
            double value = Math.exp(epsilon * getUtility(note) / (2 * SENSIBILITY));
            sumOfGeneratedValues += value;
            cumulateProbability.add(value);
        }

        if (sumOfGeneratedValues == 0) {
            throw new IllegalArgumentException(
                    "Sum of generated values is zero, cannot normalize to generate probabilities.");
        }

        for (int i = 0; i < cumulateProbability.size(); i++) {
            if (i == 0) {
                cumulateProbability.set(i, cumulateProbability.get(i) / sumOfGeneratedValues);
            } else {
                cumulateProbability.set(i,
                        cumulateProbability.get(i) / sumOfGeneratedValues + cumulateProbability.get(i - 1));
            }
        }
        return cumulateProbability;
    }

    /**
     * Generates a pair containing a list of movies and their corresponding
     * cumulative probabilities.
     *
     * @param recomendedMovieList a map of movies and their associated scores
     * @return a pair containing the list of movies and the list of cumulative
     *         probabilities
     */
    private Pair<List<Movie>, List<Double>> generateListofMovieAndListOfCumulateProbability(
            Map<Movie, Double> recomendedMovieList) {
        List<Movie> movieList = new ArrayList<>();
        List<Double> predictedEvaluation = new ArrayList<>();

        for (Map.Entry<Movie, Double> entry : recomendedMovieList.entrySet()) {
            movieList.add(entry.getKey());
            predictedEvaluation.add(entry.getValue());
        }

        Pair<List<Movie>, List<Double>> pair = new Pair<>();
        pair.setFirst(movieList);
        pair.setSecond(predictedEvaluation);
        return pair;
    }

    /**
     * Finds the index of the first element in the cumulative probability list
     * that is greater than or equal to the given random value.
     *
     * @param cumuledProbability the list of cumulative probabilities
     * @param randomValue        the random value to compare
     * @return an {@code OptionalInt} containing the index if found, or empty if not
     *         found
     */
    private OptionalInt findIndexWithRdNumber(List<Double> cumuledProbability, double randomValue) {
        for (int index = 0; index < cumuledProbability.size(); index++) {
            if (randomValue < cumuledProbability.get(index)) {
                return OptionalInt.of(index);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Selects a random movie from the recommended movie list based on the
     * Exponential Mechanism.
     *
     * @param recomendedMovieList a map of movies and their associated scores
     * @param epsilon             the privacy parameter
     * @return the selected movie
     * @throws IllegalStateException if no movie is found for the given random value
     */
    public Movie selectRandomMovie(Map<Movie, Double> recomendedMovieList, double epsilon) {
        double randomValue = rdGenerator.nextDouble();

        Pair<List<Movie>, List<Double>> pair = generateListofMovieAndListOfCumulateProbability(
                recomendedMovieList);

        List<Double> cumuledProbability = generateProbability(pair.getSecond(), epsilon);
        OptionalInt key = findIndexWithRdNumber(cumuledProbability, randomValue);
        if (key.isPresent()) {
            return pair.getFirst().get(key.getAsInt());
        } else {
            throw new IllegalStateException("No movie found for the given random value.");
        }
    }
}