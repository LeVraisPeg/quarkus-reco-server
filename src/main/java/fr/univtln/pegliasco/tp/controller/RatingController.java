package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.encryption.differential_privacy.MakeNoise;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.view.RatingId;
import fr.univtln.pegliasco.tp.services.RatingService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Path("/rating")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingController {
    @Inject
    RatingService ratingService;

    // Récupérer toutes les évaluations
    @GET
    public List<Rating> getAllRatings(@QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("1000") int size) {
        List<Rating> pagedRatings = ratingService.getRatingsPaginated(page, size);
        return MakeNoise.applyLapplaceNoise(pagedRatings);
    }


    @GET
    @Path("/fileNoise")
    @Produces("text/csv")
    public Response getAllRatingsNoisyAsCSV() {
        List<RatingId> allRatings = ratingService.getAllRatingsId();
        // Application du bruit différentiel
        List<RatingId> noisyRatings = MakeNoise.applyLapplaceNoiselist(allRatings);
        try {
            File csv = RatingService.generateCSV(noisyRatings);
            return Response.ok(csv)
                    .header("Content-Disposition", "attachment; filename=\"ratings.csv\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating CSV").build();
        }
    }

    @GET
    @Path("/file")
    @Produces("text/csv")
    public Response getAllRatingsAsCSV() {
        List<RatingId> allRatings = ratingService.getAllRatingsId();
        // Application du bruit différentiel
        try {
            File csv = RatingService.generateCSV(allRatings);
            return Response.ok(csv)
                    .header("Content-Disposition", "attachment; filename=\"ratings.csv\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating CSV").build();
        }
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
    @Path("/{id}/{value}")
    public Response updateRating(@PathParam("id") Long id,
                                 @PathParam("value") Float rate, Rating rating) {
        rating.setId(id);
        rating.setRate(rate);
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


    // findByAccountId
    @GET
    @Path("/account/{accountId}")
    public List<Rating> getRatingsByAccountId(@PathParam("accountId") Long accountId) {
        return ratingService.getRatingsByAccountId(accountId);
    }

    // recupérer la note d'un account pour un film par leur id
    @GET
    @Path("/account/{accountId}/movie/{movieId}")
    public Response getRatingByAccountIdAndMovieId(@PathParam("accountId") Long accountId,
                                                   @PathParam("movieId") Long movieId) {
        List<Rating> ratings = ratingService.getRatingByAccountIdAndMovieId(accountId, movieId);
        if (!ratings.isEmpty()) {
            return Response.ok(MakeNoise.applyLapplaceNoise(ratings)).build();
        } else {
            return Response.ok(List.of()).build(); // Renvoie une liste vide
        }
    }

    // Récupérer le movie avec un rating id
    @GET
    @Path("/ratingId/{ratingId}")
    public Response getMovieByRatingId(@PathParam("ratingId") Long ratingId) {
        Movie movie = ratingService.getMovieByRatingId(ratingId);
        if (movie != null) {
            return Response.ok(movie).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}