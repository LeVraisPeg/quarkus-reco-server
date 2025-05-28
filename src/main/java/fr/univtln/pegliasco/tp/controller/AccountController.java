package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.RatingCache;
import fr.univtln.pegliasco.tp.model.nosql.Mapper.MovieMapper;
import fr.univtln.pegliasco.tp.services.*;
import fr.univtln.pegliasco.tp.model.Account;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.MovieElastic;

import fr.univtln.pegliasco.tp.services.MovieService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;

import java.util.logging.Logger;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {
    private static final Logger LOGGER = Logger.getLogger(AccountController.class.getName());
    @Inject
    AccountService accountService;
    @Inject
    MovieService movieService;
    @Inject
    RatingCacheService ratingCacheService;
    @Inject
    RatingService ratingService;
    @Inject
    MovieElasticService movieElasticService;

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
            @PathParam("password") String password) {
        Account existingAccount = accountService.getAccountById(id);
        if (existingAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingAccount.setId(id);
        existingAccount.setNom(nom);
        existingAccount.setPrenom(prenom);
        existingAccount.setEmail(email);
        existingAccount.setPassword(password);
        accountService.updateAccount(existingAccount);
        return Response.ok(existingAccount).build();
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
            @PathParam("movieId") Long movieId,@PathParam("rate") Float rate) throws IOException {
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        MovieElastic movieElastic = movieElasticService.getMovieById(movieId);
        if (movieElastic == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        Movie movie = MovieMapper.fromElastic(movieElastic);

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

    //Supprimer un rating d'un film dans le cache et dans la base de données
    @DELETE
    @Path("/{accountId}/movie/{movieId}/rating")
    public Response deleteRating(@PathParam("accountId") Long accountId,
            @PathParam("movieId") Long movieId) {
        LOGGER.info("Requête suppression rating pour accountId=" + accountId + ", movieId=" + movieId);
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            LOGGER.warning("Compte non trouvé pour id=" + accountId);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) {
            LOGGER.warning("Film non trouvé pour id=" + movieId);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ratingCacheService.deleteRatingFromCache(accountId, movieId);
        ratingService.deleteRating(accountId, movieId);
        LOGGER.info("Suppression terminée pour accountId=" + accountId + ", movieId=" + movieId);
        return Response.noContent().build();
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