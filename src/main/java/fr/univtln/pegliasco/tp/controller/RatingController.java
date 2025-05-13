package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.encryption.differential_privacy.MakeNoise;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.services.RatingService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/rating")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingController {
    @Inject
    RatingService ratingService;

    // Récupérer toutes les évaluations
    @GET
    public List<Rating> getAllRatings() {
        return MakeNoise.applyLapplaceNoise(ratingService.getAllRatings());
    }

    // Ajouter une évaluation
    @POST
    public Response addRating(Rating rating) {
        ratingService.addRating(rating);
        return Response.status(Response.Status.CREATED).entity(rating).build();
    }

    // Supprimer une évaluation par son ID
    @DELETE
    @Path("/{id}")
    public Response deleteRating(@PathParam("id") Long id) {
        ratingService.deleteRating(id);
        return Response.noContent().build();
    }

    // Mettre à jour une évaluation par son ID
    @PUT
    @Path("/{id}")
    public Response updateRating(@PathParam("id") Long id, Rating rating) {
        ratingService.updateRating(id, rating);
        return Response.ok(rating).build();
    }

    // Récupérer une évaluation par son ID
    @GET
    @Path("/{id}")
    public Response getRatingById(@PathParam("id") Long id) {
        Rating rating = ratingService.getRatingById(id);
        if (rating != null) {
            return Response.ok(MakeNoise.applyLapplaceNoise(rating)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Récupérer la note d'un utilisateur pour un film par leurs IDs
    @GET
    @Path("/user/{userId}/movie/{movieId}")
    public Response getRatingByUserIdAndMovieId(@PathParam("userId") Long userId, @PathParam("movieId") Long movieId) {
        Rating rating = ratingService.getRatingByAccountIdAndMovieId(userId, movieId);
        if (rating != null) {
            return Response.ok(MakeNoise.applyLapplaceNoise(rating)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // findByAccountId
    @GET
    @Path("/account/{accountId}")
    public List<Rating> getRatingsByAccountId(@PathParam("accountId") Long accountId) {
        return ratingService.getRatingsByAccountId(accountId);
    }
}
