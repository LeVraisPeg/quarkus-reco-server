package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.repository.RatingRepository;
import fr.univtln.pegliasco.tp.services.RecommendedService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.function.Function;

import fr.univtln.pegliasco.encryption.differential_privacy.ExponentialMechanism;
import jakarta.ws.rs.core.Response;

/**
 * REST controller that provides movie recommendations for users.
 *
 * This controller exposes an endpoint to get personalized or cold-start movie
 * recommendations.
 * If the user has rated movies, recommendations are personalized; otherwise, a
 * generic list is provided.
 * The recommendations are further filtered using the MovieRandomSelector, which
 * applies
 * the Exponential Mechanism for differential privacy.
 *
 * Example usage:
 * GET /api/recommend?id=123&nb=5
 * Returns a JSON list of 5 recommended movies for user 123.
 */

@Path("/api/recommend")
@Produces(MediaType.APPLICATION_JSON)
public class RecommendedController {
    @Inject
    RecommendedService recommendedService;
    @Inject
    RatingRepository ratingRepository;

    @GET
    public List<Movie> getRecommendations(@QueryParam("id") Long id, @QueryParam("nb") int nb) {
        if (ratingRepository.hasNumberOfRatingsAboveLimit(id, 0)) {
            return recommendedService.fetchRecommendations(id, nb);
        }
        List<Movie> movies = recommendedService.fetchColdRecommendations(2 * nb);
        Function<Movie, Double> utilityFunction = movie -> Math
                .abs(ratingRepository.getAverageRating(movie.getId()) - 2.5) / 2.5;
        ExponentialMechanism selector = new ExponentialMechanism(movies, utilityFunction);
        return selector.selectRandomMovies(nb);

    }

    @POST
    @Path("/init")
    public Response init() {
        recommendedService.init();
        return Response.noContent().build();
    }
}
