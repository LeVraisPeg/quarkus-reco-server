package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.RatingCache;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import fr.univtln.pegliasco.tp.services.RatingCacheService;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/ratingCache")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingCacheController {
    @Inject
    RatingCacheService ratingCacheService;

    // Récupérer toutes les évaluations en cache
    @GET
    public List<RatingCache> getAllRatingsFromCache(@QueryParam("page") @DefaultValue("0") int page,
                                                    @QueryParam("size") @DefaultValue("1000") int size) {
        return ratingCacheService.getRatingsPaginatedFromCache(page, size);
    }

    // Ajouter une évaluation au cache
    @POST
    public Response addRatingToCache(RatingCache ratingCache) {
        ratingCacheService.addRatingToCache(ratingCache);
        return Response.status(Response.Status.CREATED).entity(ratingCache).build();
    }

    // Supprimer une évaluation du cache par son ID
    @DELETE
    @Path("/{id}")
    public Response deleteRatingFromCache(@PathParam("id") Long id) {
        ratingCacheService.deleteRatingFromCache(id);
        return Response.noContent().build();
    }

    //Recupérer une évaluation en cache par son ID accountId
    @GET
    @Path("/cache/{accountId}")
    public Response getRatingCacheByAccountId(@PathParam("accountId") Long accountId) {
        List<RatingCache> ratings = ratingCacheService.getRatingCacheByAccountId(accountId);
        if (ratings != null && !ratings.isEmpty()) {
            return Response.ok(ratings).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Mettre à jour une évaluation dans le cache par son ID
    @PUT
    @Path("/{id}/{rating}")
    public Response updateRatingInCache(@PathParam("id") Long id, @PathParam("rating") float rating) {
        RatingCache existingRating = ratingCacheService.getRatingCacheById(id);
        if (existingRating == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingRating.setRate(rating);
        ratingCacheService.updateRatingInCache(existingRating);
        return Response.ok(existingRating).build();
    }

    // Récupérer une évaluation en cache par son ID
    @GET
    @Path("/{id}")
    public Response getRatingCacheById(@PathParam("id") Long id) {
        RatingCache rating = ratingCacheService.getRatingCacheById(id);
        if (rating != null) {
            return Response.ok(rating).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
