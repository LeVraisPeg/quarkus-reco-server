package fr.univtln.pegliasco.tp.movies_to_recommend;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.pegliasco.encryption.differential_privacy.ExponentialMechanism;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.repository.MovieRepository;

public class ListOfRecommendedMovies {

    private MovieRepository movieRepository = new MovieRepository();
    private List<Movie> moviesToRecommend = new ArrayList<>();
    private List<RecommendedMovie> recommendedMovies;

    public ListOfRecommendedMovies(List<RecommendedMovie> recommendedMovies) {
        this.recommendedMovies = recommendedMovies;
    }

    private void selectMovie(RecommendedMovie selectedMovie) {
        recommendedMovies.removeIf(movie -> {
            if (selectedMovie.getMovieId().equals(movie.getMovieId())) {
                Movie movieToRecommend = movieRepository.findById(movie.getMovieId());
                moviesToRecommend.add(movieToRecommend);
                return true;
            } else {
                return false;
            }
        });
    }

    public List<Movie> getMovies(int numberOfMovies) {
        if (numberOfMovies <= 0 || numberOfMovies > recommendedMovies.size()
                || recommendedMovies.size() < numberOfMovies) {
            throw new IllegalArgumentException("Invalid number of movies requested.");
        } else if (numberOfMovies == recommendedMovies.size()) {
            for (RecommendedMovie movie : recommendedMovies) {
                Movie movieToRecommend = movieRepository.findById(movie.getMovieId());
                moviesToRecommend.add(movieToRecommend);
            }
            return moviesToRecommend;
        } else {
            for (int i = 0; i < numberOfMovies; i++) {
                RecommendedMovie movie = ExponentialMechanism.selectRandomMovie(recommendedMovies, 0.5);
                selectMovie(movie);
            }
        }
        return moviesToRecommend;
    }
}