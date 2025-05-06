package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.services.AdminService;
import fr.univtln.pegliasco.tp.model.Admin;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminController {
    @Inject
    AdminService adminService;

    // Récupérer tous les administrateurs
    @GET
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }



}
