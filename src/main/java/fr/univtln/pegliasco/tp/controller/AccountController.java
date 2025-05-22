package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.RatingCache;
import fr.univtln.pegliasco.tp.services.AccountService;
import fr.univtln.pegliasco.tp.model.Account;

import fr.univtln.pegliasco.tp.services.MovieService;
import fr.univtln.pegliasco.tp.services.RatingCacheService;
import fr.univtln.pegliasco.tp.services.RatingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {
    @Inject
    AccountService accountService;
    @Inject
    MovieService movieService;
    @Inject
    RatingCacheService ratingCacheService;
    @Inject
    RatingService ratingService;

    // Récupérer tous les comptes
    @GET
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Récupérer un compte par son ID
    @GET
    @Path("/{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        Account account = accountService.getAccountById(id);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(account).build();
    }

    // Ajouter un compte
    @POST
    public Response addAccount(Account account) {
        accountService.addAccount(account);
        return Response.status(Response.Status.CREATED).build();
    }

    // Mettre à jour un compte
    @PUT
    @Path("/{id}/{nom}/{prenom}/{email}/{password}/{Role}")
    public Response updateAccount(@PathParam("id") Long id, @PathParam("nom") String nom,
            @PathParam("prenom") String prenom, @PathParam("email") String email,
            @PathParam("password") String password, Account account) {
        Account existingAccount = accountService.getAccountById(id);
        if (existingAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        account.setId(id);
        account.setNom(nom);
        account.setPrenom(prenom);
        account.setEmail(email);
        account.setPassword(password);
        accountService.updateAccount(account);
        return Response.ok(account).build();
    }

    // Supprimer un compte
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        Account existingAccount = accountService.getAccountById(id);
        if (existingAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        accountService.deleteAccount(id);
        return Response.noContent().build();
    }

    // Récupérer un compte par son nom d'utilisateur
    @GET
    @Path("/username/{username}")
    public Response getAccountByUsername(@PathParam("username") String username) {
        Account account = accountService.getAccountByNom(username);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(account).build();
    }

    // Authentification (nom d'utilisateur et mot de passe) renvoie l'id de
    // l'utilisateur
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        Account account = accountService.authenticate(username, password);
        if (account == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(account).build();
    }

    // Récupérer un compte par son rôle
    @GET
    @Path("/role/{role}")
    public Response getAccountByRole(@PathParam("role") String role) {
        List<Account> accounts = accountService.getAccountByRole(role);
        return Response.ok(accounts).build();
    }

    //recuperer les ratings d'un account
    @GET
    @Path("/ratings/{id}")
    public Response getRatingsByAccountId(@PathParam("id") Long id) {
        List<Rating> ratings = accountService.getRatingsByAccountId(id);
        if (ratings == null || ratings.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ratings).build();
    }

    //Noter un film
    @POST
    @Path("/{accountId}/movie/{movieId}/{rate}/rating")
    public Response rateMovie(@PathParam("accountId") Long accountId,
            @PathParam("movieId") Long movieId,@PathParam("rate") Float rate) {
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Movie movie = movieService.getMovieById(movieId);
        RatingCache ratingcache = new RatingCache();
        Rating rating = new Rating();
        rating.setAccount(account);
        ratingcache.setAccount(account);

        rating.setMovie(movie);
        ratingcache.setMovie(movie);

        rating.setRate(rate);
        ratingcache.setRate(rate);

        ratingCacheService.addRatingToCache(ratingcache);
        ratingService.addRating(rating);
        return Response.status(Response.Status.CREATED).entity(rating).build();
    }

    //Passez tout les ratingscache au rating du compte
    @POST
    @Path("ratings/{accountId}")
    public Response passAllRatingsCacheToRating(@PathParam("accountId") Long accountId) {
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<RatingCache> caches = ratingCacheService.getRatingCacheByAccountId(accountId);
        for (RatingCache cache : caches) {
            Rating rating = new Rating();
            rating.setAccount(account);
            rating.setMovie(cache.getMovie());
            rating.setRate(cache.getRate());
            account.getRatings().add(rating);
            ratingCacheService.deleteRatingFromCache(cache.getId());
        }
        accountService.updateAccount(account);
        return Response.ok().build();
    }

}