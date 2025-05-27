package fr.univtln.pegliasco.encryption.differential_privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Function;

import fr.univtln.pegliasco.tp.model.Movie;

/**
 * Cette classe implémente le mécanisme exponentiel pour la sélection de films
 * avec confidentialité différentielle, et gère la logique de recommandation.
 */
public class ExponentialMechanism {
    private static final Random randomGenerator = new Random();

    private List<Movie> recommendedMovies;
    private List<Movie> moviesInRecommendation = new ArrayList<>();
    private Function<Movie, Double> utilityFunction;

    public ExponentialMechanism(List<Movie> recommendedMovies, Function<Movie, Double> utilityFunction) {
        this.recommendedMovies = recommendedMovies;
        this.utilityFunction = utilityFunction;
    }

    /**
     * Ajoute un film à la liste de recommandation et le retire de la liste des
     * films recommandés.
     */
    private void addMovieToRecommendation(Movie selectedMovie) {
        recommendedMovies.removeIf(movie -> {
            if (selectedMovie.getId().equals(movie.getId())) {
                moviesInRecommendation.add(selectedMovie);
                return true;
            }
            return false;
        });
    }

    /**
     * Sélectionne un film aléatoirement selon le mécanisme exponentiel.
     */
    private Movie selectRandomMovie(List<Movie> movies, double epsilon) {
        if (movies == null || movies.isEmpty()) {
            throw new IllegalArgumentException(
                    "The recommended movies list cannot be null or empty, and the number of movies must be positive and less than or equal to the size of the list.");
        }

        double randomValue = randomGenerator.nextDouble();
        List<Double> cumulativeProbabilities = generateCumulativeProbabilities(movies, epsilon);
        OptionalInt index = findIndexWithRandomValue(cumulativeProbabilities, randomValue);

        if (index.isPresent()) {
            return movies.get(index.getAsInt());
        } else {
            throw new IllegalStateException("No movie found for the given random value.");
        }
    }

    /**
     * Sélectionne un nombre donné de films aléatoires selon le mécanisme
     * exponentiel.
     */
    public List<Movie> selectRandomMovies(int numberOfMovies) {
        if (numberOfMovies <= 0) {
            throw new IllegalArgumentException("Invalid number of movies requested.");
        }
        // On sélectionne dans recommendedMovies, pas dans moviesInRecommendation
        List<Movie> selected = new ArrayList<>();
        for (int i = 0; i < numberOfMovies && !recommendedMovies.isEmpty(); i++) {
            Movie movie = selectRandomMovie(recommendedMovies, 0.5);
            addMovieToRecommendation(movie);
            selected.add(movie);
        }
        return selected;
    }

    /**
     * Génère la distribution cumulative des probabilités.
     */
    private List<Double> generateCumulativeProbabilities(List<Movie> movies, double epsilon) {
        if (movies == null || movies.isEmpty()) {
            throw new IllegalArgumentException("The predicted ratings list cannot be null or empty.");
        }

        double[] values = new double[movies.size()];
        double sumOfValues = 0.0;
        for (int i = 0; i < movies.size(); i++) {
            values[i] = Math.exp(epsilon * utilityFunction.apply(movies.get(i)) / 2);
            sumOfValues += values[i];
        }

        if (sumOfValues == 0) {
            throw new IllegalArgumentException(
                    "Sum of generated values is zero, cannot normalize to generate probabilities.");
        }

        List<Double> cumulativeProbabilities = new ArrayList<>(movies.size());
        double cumulative = 0.0;
        for (double value : values) {
            cumulative += value / sumOfValues;
            cumulativeProbabilities.add(cumulative);
        }
        return cumulativeProbabilities;
    }

    /**
     * Trouve l'index correspondant à la valeur aléatoire dans la distribution
     * cumulative.
     */
    private OptionalInt findIndexWithRandomValue(List<Double> cumulativeProbabilities, double randomValue) {
        for (int index = 0; index < cumulativeProbabilities.size(); index++) {
            if (randomValue < cumulativeProbabilities.get(index)) {
                return OptionalInt.of(index);
            }
        }
        return OptionalInt.empty();
    }
}