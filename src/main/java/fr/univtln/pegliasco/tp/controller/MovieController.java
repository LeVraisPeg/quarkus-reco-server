package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.services.MovieService;
import fr.univtln.pegliasco.tp.model.Movie;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {
    @Inject
    MovieService movieService;

    // Récupérer tous les films
    @GET
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
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
    @Path("/{id}")
    public Response updateMovie(@PathParam("id") Long id, Movie movie) {
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

    //Récupérer les notes d'un film par son ID
    @GET
    @Path("/rating/{movieId}")
    public Response getRatingsByMovieId(@PathParam("movieId") Long movieId) {
        List<Rating> ratings = movieService.getRatingsByMovieId(movieId);
        if (ratings != null && !ratings.isEmpty()) {
            return Response.ok(ratings).build();
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
        List<Movie> movies = movieService.getMoviesByTitle(title);
        if (movies != null && !movies.isEmpty()) {
            return Response.ok(movies).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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

}
