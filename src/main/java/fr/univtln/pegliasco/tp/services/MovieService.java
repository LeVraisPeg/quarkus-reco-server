package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Gender;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.MovieElastic;
import fr.univtln.pegliasco.tp.model.nosql.Mapper.MovieMapper;
import fr.univtln.pegliasco.tp.repository.GenderRepository;
import fr.univtln.pegliasco.tp.repository.MovieRepository;
import fr.univtln.pegliasco.tp.repository.TagRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

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

    @Inject
    TagRepository tagRepository;
    @Inject
    GenderRepository genderRepository;



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



    @Transactional
    public void deleteMovieAndCleanup(Long id) throws IOException {
        //Logger log = Logger.getLogger(MovieService.class);

        //log.infof("Suppression du film avec l'id %d : début du processus.", id);
        Movie movie = movieRepository.findById(id);
        if (movie == null) {
            //log.warnf("Aucun film trouvé avec l'id %d. Suppression annulée.", id);
            return;
        }
        //Detacher le film des notes (propriétaire = Rating)
        if (movie.getRatings() != null) {
            //log.infof("Détachement du film des %d notes associées.", movie.getRatings().size());
            for (Rating rating : movie.getRatings()) {
                rating.setMovie(null); // côté inverse
                //log.debugf("Note retirée du film id=%d.", rating.getId());
            }
            movie.getRatings().clear(); // côté propriétaire
        }

        // Détacher le film des tags (propriétaire = Tag)
        if (movie.getTags() != null) {
            //log.infof("Détachement du film des %d tags associés.", movie.getTags().size());
            for (Tag tag : movie.getTags()) {
                tag.getMovies().remove(movie);
                tagRepository.update(tag);
                //log.debugf("Film retiré du tag id=%d.", tag.getId());
            }
            movie.getTags().clear();
        }

        // Détacher le film des genres (propriétaire = Movie)
        if (movie.getGenders() != null) {
            //log.infof("Détachement du film des %d genres associés.", movie.getGenders().size());
            for (Gender gender : movie.getGenders()) {
                gender.getMovies().remove(movie); // côté inverse
            genderRepository.update(gender);
                //log.debugf("Film retiré du genre id=%d.", gender.getId());
            }
            movie.getGenders().clear(); // côté propriétaire
        }

        //log.infof("Suppression du film dans Elasticsearch (id=%d).", id);
        movieElasticService.deleteMovie(id);

        //log.infof("Suppression du film en base (id=%d).", id);
        movieRepository.delete(id);

        //log.infof("Suppression du film avec l'id %d terminée.", id);
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