package fr.univtln.pegliasco.tp.Interface;

import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Path("/")
@RegisterRestClient(configKey = "recommendation-api")
public interface RecommendedInterface {


    @POST
    @Path("/init_recommendation")
    void initRecommender();


    /**
     * Récupérer les recommandations pour un utilisateur donné
     *
     * @param userId l'ID de l'utilisateur
     * @param count  le nombre de recommandations à récupérer
     * @return une liste d'IDs de films recommandés
     *
     */
    @GET
    @Path("/recommendations/{userId}")
    List<Long> getRecommendations(@PathParam("userId") Long userId,
                                          @QueryParam("top_n") int count);

    @GET
    @Path("/cold_recommendation")
    List<Long> getColdRecommendations(@QueryParam("top_n") int count);


}
