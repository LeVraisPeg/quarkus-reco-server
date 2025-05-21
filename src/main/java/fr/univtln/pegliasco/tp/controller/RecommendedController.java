package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.services.RecommendedService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/recommend")
@Produces(MediaType.APPLICATION_JSON)
public class RecommendedController {
    @Inject
    RecommendedService recommendedService;

    @GET
    public List<Movie> getRecommendations(@QueryParam("id") Long id, @QueryParam("nb") int nb) {
        return recommendedService.fetchRecommendations(id, nb);
    }
    @GET
    @Path("/cold_recommendation")
    public List<Movie> getColdRecommendations(@QueryParam("nb") int nb) {
        return recommendedService.fetchColdRecommendations(nb);
    }
}
