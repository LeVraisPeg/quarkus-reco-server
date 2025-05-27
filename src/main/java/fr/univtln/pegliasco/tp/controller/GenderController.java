package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Gender;
import fr.univtln.pegliasco.tp.services.GenderService;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.GenderElastic;
import fr.univtln.pegliasco.tp.services.GenderElasticService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;

@Path("/gender")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GenderController {
    @Inject
    GenderService genderService;
    @Inject
    GenderElasticService genderElasticService;

    // Récupérer tous les genres
    @GET
    public List<GenderElastic> getAllGenders() {
        try {
            return genderElasticService.getAllGenders();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Ajouter un genre
    @POST
    public Response addGender(Gender gender){
        genderService.addGender(gender);
        return Response.status(Response.Status.CREATED).entity(gender).build();
    }

    // Supprimer un genre par son ID
    @DELETE
    @Path("/{id}")
    public Response deleteGender(@PathParam("id") Long id) {
        genderService.deleteGender(id);
        return Response.noContent().build();
    }
    // Mettre à jour un genre par son ID
    @PUT
    @Path("/{id}/{name}")
    public Response updateGender(@PathParam("id")Long id,@PathParam("name")String name,Gender gender){
        gender.setName(name);
        genderService.updateGender(id,gender);
        return Response.ok(gender).build();
    }

    // Récupérer un genre par son ID
    @GET
    @Path("/{id}")
    public Response getGenderById(@PathParam("id") Long id){
        Gender gender = genderService.getGenderById(id);
        if (gender != null) {
            return Response.ok(gender).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Trouver ou créer un genre par son nom
    @GET
    @Path("/{name}")
    public Response findOrCreateGender(@PathParam("name") String name) {
        Gender gender = genderService.findOrCreateByName(name);
        return Response.ok(gender).build();
    }


}
