package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.model.FolderPathRequest;
import fr.univtln.pegliasco.tp.services.CsvImporterService;
import fr.univtln.pegliasco.tp.model.ApiResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImportController {

    @Inject
    CsvImporterService csvImporterService;

    @POST
    @Path("/ratings")
    public Response importRatingsFromCsv(FolderPathRequest folderPathRequest) {
        try {
            // Traiter le chemin du dossier
            csvImporterService.importRatingsFromCsv(folderPathRequest.getFolderPath());

            return Response.ok(new ApiResponse("CSV import completed successfully.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse("Error occurred during CSV import: " + e.getMessage()))
                    .build();
        }
    }
    @POST
    @Path("/movies")
    public Response importMoviesFromCsv(FolderPathRequest folderPathRequest) {
        try {
            csvImporterService.importMoviesFromCsv(folderPathRequest.getFolderPath());

            return Response.ok(new ApiResponse("Movie CSV import completed successfully.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse("Error occurred during movie CSV import: " + e.getMessage()))
                    .build();
        }
    }




}
