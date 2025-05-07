package fr.univtln.pegliasco.tp.controller;

import fr.univtln.pegliasco.tp.services.CsvImporterService;

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


    // Class interne pour la réponse API
    public static class ApiResponse {
        public String message;

        public ApiResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class FolderPathRequest {
        private String folderPath;

        public String getFolderPath() {
            return folderPath;
        }

        public void setFolderPath(String folderPath) {
            this.folderPath = folderPath;
        }
    }
}
