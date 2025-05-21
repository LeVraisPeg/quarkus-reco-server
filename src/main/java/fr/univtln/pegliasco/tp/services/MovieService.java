package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Gender;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.model.nosql.MovieElastic;
import fr.univtln.pegliasco.tp.model.nosql.MovieMapper;
import fr.univtln.pegliasco.tp.repository.MovieRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MovieService {
    private final MovieRepository movieRepository;

    @Inject
    MovieElasticService movieElasticService;



    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // generateCSV
    public static File generateCSV(List<Movie> movies) throws IOException {
        File tempFile = File.createTempFile("movies", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.append("movieId,title,released,runtime,genre,director,writer,actors,plot,country\n");
            for (Movie m : movies) {
                writer.append(String.valueOf(m.getId())).append(",")
                        .append('"' + String.valueOf(m.getTitle()) + '"').append(",")
                        .append(String.valueOf(m.getYear())).append(",")
                        .append(String.valueOf(m.getRuntime())).append(",")
                        .append('"' + String.join("|", m.getGenders()
                                .stream().map(Gender::getName).toList()) + '"')
                        .append(",")
                        .append('"' + String.valueOf(m.getDirector()) + '"').append(",")
                        .append('"' + String.join("|", m.getWriters()) + '"').append(",")
                        .append('"' + String.join("|", m.getActors()) + '"').append(",")
                        .append('"' + String.valueOf(m.getPlot()) + '"').append(",")
                        .append('"' + String.valueOf(m.getCountry()) + '"').append("\n");
            }
        }
        return tempFile;
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

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    // getMoviesPaginated
    public List<Movie> getMoviesPaginated(int page, int size) {
        List<Movie> movies = movieRepository.findPaginated(page, size);
        movies.forEach(movie -> {
            if (movie.getRatings() != null) {
                movie.getRatings().size();
            }
        });
        return movies;
    }

    public List<Movie> getMoviesByTitleContainsIgnoreCase(String title) {
        return movieRepository.findByTitleSmart(title);
    }

    // getMoviesByIds

    public List<Movie> getMoviesByIds(List<Long> ids) {
        return movieRepository.findByIds(ids);
    }

    @Transactional
    public void addMovie(Movie movie) {
        movieRepository.save(movie);
        try {
            MovieElastic movieElastic = MovieMapper.toElastic(movie);
            movieElasticService.indexMovie(movieElastic);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // Récupérer les notes d'un film par son ID

    public List<Rating> getRatingsByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId);
        if (movie != null) {
            return movie.getRatings();
        } else {
            return null;
        }
    }

    // Récupérer la note moyenne d'un film par son ID

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

    public List<Movie> findByGender(String gender) {
        return movieRepository.findByGender(gender);
    }

    // Récupérer les films par tag

    public List<Movie> getMoviesByTag(String tag) {
        return movieRepository.findByTag(tag);
    }

    // Récupérer les films par titre

    public List<Movie> getMoviesByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    // Récupérer les films par année
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

    // find or create by id
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

    // findAllAsMap
    public Map<Long, Movie> findAllAsMap() {
        return movieRepository.findAllAsMap();
    }

    // Récupérer les tags d'un film par son ID
    public List<Tag> getTagsByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId);
        if (movie != null) {
            return movie.getTags();
        } else {
            return null;
        }
    }

    public List<MovieElastic> searchMovies(String keyword) throws IOException {
        return movieElasticService.searchMovies(keyword);
    }
}