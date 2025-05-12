package fr.univtln.pegliasco.encryption.differential_privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.logging.Logger;

import fr.univtln.pegliasco.tp.model.Movie;

public class ExponentialMechanism {
    private static final int MAX_NOTATION = 5;
    private static final double SENSIBILITY = 0.5;
    private final Random rdGenerator = new Random();
    Logger l = Logger.getLogger(ExponentialMechanism.class.getName());

    private double getUtility(double note) {
        return -Math.abs(note - MAX_NOTATION);
    }

    private List<Double> generateRepartition(List<Double> predectedNotation, double epsilon) {
        List<Double> cumulateProbability = new ArrayList<>();
        double sumOfGeneratedValues = 0;
        for (double note : predectedNotation) {
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

    private Pair<List<Movie>, List<Double>> generateListofMovieAndListOfCumulateProbability(
            Map<Movie, Double> recomendedMovieList) {
        List<Movie> movieList = new ArrayList<>();
        List<Double> cumulateProbability = new ArrayList<>();

        for (Map.Entry<Movie, Double> entry : recomendedMovieList.entrySet()) {
            movieList.add(entry.getKey());
            cumulateProbability.add(entry.getValue());
        }

        Pair<List<Movie>, List<Double>> pair = new Pair<>();
        pair.setFirst(movieList);
        pair.setSecond(cumulateProbability);
        return pair;
    }

    private OptionalInt findIndexWithRdNumber(List<Double> cumuledProbability, double randomValue) {
        for (int index = 0; index < cumuledProbability.size(); index++) {
            if (randomValue < cumuledProbability.get(index)) {
                return OptionalInt.of(index);
            }
        }
        return OptionalInt.empty();
    }

    public Movie selectRandomMovie(Map<Movie, Double> recomendedMovieList, double epsilon) {
        double randomValue = rdGenerator.nextDouble();

        Pair<List<Movie>, List<Double>> pair = generateListofMovieAndListOfCumulateProbability(
                recomendedMovieList);

        List<Double> cumuledProbability = generateRepartition(pair.getSecond(), epsilon);
        OptionalInt key = findIndexWithRdNumber(cumuledProbability, randomValue);
        if (key.isPresent()) {
            return pair.getFirst().get(key.getAsInt());
        } else {
            throw new IllegalStateException("No movie found for the given random value.");
        }
    }
}