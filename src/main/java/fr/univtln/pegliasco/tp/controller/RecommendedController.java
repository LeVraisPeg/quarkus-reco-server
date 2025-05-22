package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.repository.RatingRepository;
import fr.univtln.pegliasco.tp.services.RecommendedService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import fr.univtln.pegliasco.tp.model.Rating;

@Path("/api/recommend")
@Produces(MediaType.APPLICATION_JSON)
public class RecommendedController {
    @Inject
    RecommendedService recommendedService;
    @Inject
    RatingRepository ratingRepository;

    @GET
    public List<Movie> getRecommendations(@QueryParam("id") Long id, @QueryParam("nb") int nb) {
        List<Rating> ratings = ratingRepository.findByAccountId(id);
        if (ratings.isEmpty()) {
            return recommendedService.fetchColdRecommendations(nb);
        }
        return recommendedService.fetchRecommendations(id, nb);
    }
}
