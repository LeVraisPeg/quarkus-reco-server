package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.model.nosql.MovieElastic;
import fr.univtln.pegliasco.tp.services.MovieService;
import fr.univtln.pegliasco.encryption.differential_privacy.MakeNoise;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.services.MovieElasticService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@Path("/movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {
    @Inject
    MovieService movieService;

    @Inject
    MovieElasticService movieElasticService;

    Logger logger = Logger.getLogger(MovieController.class.getName());

    // Récupérer tous les films
    @GET
    public List<Movie> getAllMovies(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("1000") int size) {
        List<Movie> pagedMovies = movieService.getMoviesPaginated(page, size);
        return pagedMovies;

    }

    // Ajouter un film
    @POST
    public Response addMovie(Movie movie) {
        movieService.addMovie(movie);
        return Response.status(Response.Status.CREATED).entity(movie).build();
    }

    // Supprimer un film par son ID
    @DELETE
    @Path("/{id}")
    public Response deleteMovie(@PathParam("id") Long id) {
        movieService.deleteMovie(id);
        return Response.noContent().build();
    }

    // Mettre à jour un film par son ID
    @PUT
    @Path("/{id}/{title}/{year}/{director}/{runtime}/{plot}/{country}/{poster}")
    public Response updateMovie(@PathParam("id") Long id, @PathParam("title") String title,
            @PathParam("year") Integer year, @PathParam("director") String director,
            @PathParam("runtime") Integer runtime, @PathParam("plot") String plot, @PathParam("country") String country,
            @PathParam("poster") String poster, Movie movie) {
        movie.setTitle(title);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        try {
            movie.setYear((dateFormat.parse(year.toString())));
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format").build();
        }
        movie.setDirector(director);
        movie.setRuntime(runtime);
        movie.setPlot(plot);
        movie.setCountry(country);
        movie.setPoster(poster);

        movieService.updateMovie(id, movie);
        return Response.ok(movie).build();
    }

    // Récupérer un film par son ID
    @GET
    @Path("/{id}")
    public Response getMovieById(@PathParam("id") Long id) {
        Movie movie = movieService.getMovieById(id);
        if (movie != null) {
            return Response.ok(movie).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Récupérer les notes d'un film par son ID
    @GET
    @Path("/rating/{movieId}")
    public Response getRatingsByMovieId(@PathParam("movieId") Long movieId) {
        List<Rating> ratings = movieService.getRatingsByMovieId(movieId);
        if (ratings != null && !ratings.isEmpty()) {
            return Response.ok(MakeNoise.applyLapplaceNoise(ratings)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Récupérer la note moyenne d'un film par son ID
    @GET
    @Path("/average/{movieId}")
    public Response getAverageRatingByMovieId(@PathParam("movieId") Long movieId) {
        double averageRating = movieService.getAverageRatingByMovieId(movieId);
        return Response.ok(averageRating).build();
    }

    // Récupérer les films par genre
    @GET
    @Path("/genre/{genre}")
    public Response findByGender(@PathParam("genre") String genre) {
        List<Movie> movies = movieService.findByGender(genre);
        if (movies != null && !movies.isEmpty()) {
            return Response.ok(movies).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Récupérer les films par tag
    @GET
    @Path("/tag/{tag}")
    public Response getMoviesByTag(@PathParam("tag") String tag) {
        List<Movie> movies = movieService.getMoviesByTag(tag);
        if (movies != null && !movies.isEmpty()) {
            return Response.ok(movies).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Récupérer les films par titre
    @GET
    @Path("/title/{title}")
    public Response getMoviesByTitle(@PathParam("title") String title) {
        List<Movie> movies = movieService.getMoviesByTitleContainsIgnoreCase(title);
        if (movies != null && !movies.isEmpty()) {
            return Response.ok(movies).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MovieElastic> search(@QueryParam("q") String query) throws IOException {
        return movieElasticService.searchMovies(query);
    }

    // Récupérer les films par année
    @GET
    @Path("/year/{year}")
    public Response getMoviesByYear(@PathParam("year") int year) {
        List<Movie> movies = movieService.getMoviesByYear(year);
        if (movies != null && !movies.isEmpty()) {
            return Response.ok(movies).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Récupérer les tags d'un film par son ID
    @GET
    @Path("/tags/{movieId}")
    public Response getTagsByMovieId(@PathParam("movieId") Long movieId) {
        List<Tag> tags = movieService.getTagsByMovieId(movieId);
        logger.info("Tags trouvés : " + tags); // Pour debug
        return Response.ok(tags != null ? tags : List.of()).build();
    }

    // getAllMoviesAsCSV
    @GET
    @Path("/file")
    @Produces("text/csv")
    public Response getAllMoviesAsCSV() {
        List<Movie> allMovies = movieService.getAllMovies();
        try {
            File csv = MovieService.generateCSV(allMovies);
            return Response.ok(csv)
                    .header("Content-Disposition", "attachment; filename=\"movies.csv\"")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating CSV").build();
        }
    }
}
