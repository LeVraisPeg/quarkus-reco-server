package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.repository.MovieRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    @Transactional
    public List<Movie> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        movies.forEach(movie -> {
            if (movie.getRatings() != null) {
                movie.getRatings().size();
            }
        });
        return movies;
    }

    @Transactional
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    //getMoviesByIds
    @Transactional
    public List<Movie> getMoviesByIds(List<Long> ids) {
        return movieRepository.findByIds(ids);
    }

    @Transactional
    public void addMovie(Movie movie) {
        movieRepository.save(movie);
    }


    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.delete(id);
    }

    // Mettre à jour un film par son ID
    @Transactional
    public void updateMovie(Long id, Movie movie) {
        Movie existingMovie = movieRepository.findById(id);
        if (existingMovie != null) {
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setYear(movie.getYear());
            existingMovie.setGenders(movie.getGenders());
            existingMovie.setRatings(movie.getRatings());
            existingMovie.setTags(movie.getTags());
            movieRepository.update(existingMovie);
        }
    }

    //Récupérer les notes d'un film par son ID
    @Transactional
    public List<Rating> getRatingsByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId);
        if (movie != null) {
            return movie.getRatings();
        } else {
            return null;
        }
    }

    // Récupérer la note moyenne d'un film par son ID
    @Transactional
    public Double getAverageRatingByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId);
        if (movie != null && !movie.getRatings().isEmpty()) {
            return movie.getRatings().stream()
                    .mapToDouble(Rating::getRate)
                    .average()
                    .orElse(0.0);
        } else {
            return null;
        }
    }

    // Récupérer les films par genre
    @Transactional
    public List<Movie> findByGender(String gender) {
        return movieRepository.findByGender(gender);
    }

    // Récupérer les films par tag
    @Transactional
    public List<Movie> getMoviesByTag(String tag) {
        return movieRepository.findByTag(tag);
    }

    // Récupérer les films par titre
    @Transactional
    public List<Movie> getMoviesByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    // Récupérer les films par année
    @Transactional
    public List<Movie> getMoviesByYear(int year) {
        return movieRepository.findByYear(year);
    }



    @Transactional
    public void saveOrUpdate(Movie movie) {
        if (movie.getId() != null && movieRepository.existsById(movie.getId())) {
            // Update existing entity
            movieRepository.merge(movie);
        } else {
            // Persist new entity
            movieRepository.persist(movie);
        }
    }

    //find or create by id
    @Transactional
    public Movie findOrCreateById(Long id) {
        Movie movie = movieRepository.findById(id);
        if (movie == null) {
            movie = new Movie();
            movie.setId(id);
            movieRepository.persist(movie);
        }
        return movie;
    }

    //findAllAsMap
    public Map<Long, Movie> findAllAsMap() {
        return movieRepository.findAllAsMap();
    }


    //Récupérer les tags d'un film par son ID
    @Transactional
    public List<Tag> getTagsByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId);
        if (movie != null) {
            return movie.getTags();
        } else {
            return null;
        }
    }

}