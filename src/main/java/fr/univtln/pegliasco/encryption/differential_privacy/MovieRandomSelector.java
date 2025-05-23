package fr.univtln.pegliasco.encryption.differential_privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import fr.univtln.pegliasco.tp.model.Movie;

/**
 * This class is responsible for selecting random movies from a list of
 * recommended movies
 * using the Exponential Mechanism for differential privacy.
 *
 * The selection process is influenced by a utility function, which determines
 * the quality
 * of each movie (for example, based on its average rating). The Exponential
 * Mechanism ensures
 * that movies with higher utility scores have a higher probability of being
 * selected, while
 * still introducing randomness to preserve privacy.
 *
 * Example usage:
 * 
 * <pre>
 * Function<Movie, Double> utilityFunction = x -> Math.abs(x - 2.5);
 * MovieRandomSelector selector = new MovieRandomSelector(movies, utilityFunction);
 * List<Movie> selected = selector.selectRandomMovies(5);
 * </pre>
 */
public class MovieRandomSelector {
    private List<Movie> recommendedMovies;
    private List<Movie> moviesInRecommendation = new ArrayList<>();
    private ExponentialMechanism exponentialMechanism;

    public MovieRandomSelector(List<Movie> recommendedMovies, Function<Movie, Double> utilityFunction) {
        this.recommendedMovies = recommendedMovies;
        this.exponentialMechanism = new ExponentialMechanism(utilityFunction);
    }

    private void addMovieToRecommendation(Movie selectedMovie) {
        recommendedMovies.removeIf(movie -> {
            if (selectedMovie.getId().equals(movie.getId())) {
                moviesInRecommendation.add(selectedMovie);
                return true;
            } else {
                return false;
            }
        });
    }

    public List<Movie> selectRandomMovies(int numberOfMovies) {
        if (numberOfMovies <= 0) {
            throw new IllegalArgumentException("Invalid number of movies requested.");
        } else if (numberOfMovies >= moviesInRecommendation.size()) {
            return moviesInRecommendation;
        } else {

            for (int i = 0; i < numberOfMovies; i++) {
                Movie movie = exponentialMechanism.selectRandomMovie(recommendedMovies, 0.5);
                addMovieToRecommendation(movie);
            }
        }
        return recommendedMovies;
    }

}
