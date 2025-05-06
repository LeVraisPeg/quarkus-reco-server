package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.services.UserService;
import fr.univtln.pegliasco.tp.model.User;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    UserService userService;

    // Récupérer tous les utilisateurs
    @GET
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
