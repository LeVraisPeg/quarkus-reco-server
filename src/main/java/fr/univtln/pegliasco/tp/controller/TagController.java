package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.services.TagService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;

@Path("/tag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagController {
    @Inject
    TagService tagService;

    // Récupérer tous les tags
    @GET
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }
    // Ajouter un tag
    @POST
    public Response addTag(Tag tag){
        tagService.addTag(tag);
        return Response.status(Response.Status.CREATED).entity(tag).build();
    }
    // Supprimer un tag par son ID
    @DELETE
    @Path("/{id}")
    public Response deleteTag(@PathParam("id") Long id) {
        tagService.deleteTag(id);
        return Response.noContent().build();
    }
    // Mettre à jour un tag par son ID
    @PUT
    @Path("/{id}")
    public Response updateTag(@PathParam("id")Long id,Tag tag){
        tagService.updateTag(id,tag);
        return Response.ok(tag).build();
    }
    // Récupérer un tag par son ID
    @GET
    @Path("/{id}")
    public Response getTagById(@PathParam("id") Long id){
        Tag tag = tagService.getTagById(id);
        if (tag != null) {
            return Response.ok(tag).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
