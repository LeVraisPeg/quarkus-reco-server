package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.services.AccountService;
import fr.univtln.pegliasco.tp.model.Account;

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
    public Response updateAccount(@PathParam("id") Long id, @PathParam("nom")String nom, @PathParam("prenom")String prenom, @PathParam("email")String email, @PathParam("password")String password, Account account) {
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

    // Authentification (nom d'utilisateur et mot de passe) renvoie l'id de l'utilisateur
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        Account account = accountService.authenticate(username, password);
        if (account == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(account.getId()).build();
    }

    // Récupérer un compte par son rôle
    @GET
    @Path("/role/{role}")
    public Response getAccountByRole(@PathParam("role") String role) {
        List<Account> accounts = accountService.getAccountByRole(role);
        return Response.ok(accounts).build();
    }
}