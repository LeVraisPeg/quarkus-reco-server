package fr.univtln.pegliasco.tp.Interface;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/recommendations")
@RegisterRestClient(configKey = "recommendation-api")
public interface RecommendedInterface {

    @GET
    @Path("/{userId}")
    List<List<Object>> getRecommendations(@PathParam("userId") Long userId,
                                          @QueryParam("top_n") int count);
}

