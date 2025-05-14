package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.Interface.RecommendedInterface;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class RecommendedService {

    @RestClient
    RecommendedInterface recommendedInterface;

    public List<List<Object>> fetchRecommendations(Long userId, int count) {
        return recommendedInterface.getRecommendations(userId, count);
    }
}

