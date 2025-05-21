package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.Interface.RecommendedInterface;
import fr.univtln.pegliasco.tp.model.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RecommendedService {

    @RestClient
    RecommendedInterface recommendedInterface;

    @Inject
    MovieService movieService;

    public List<Movie> fetchRecommendations(Long userId, int count) {
        List<List<Object>> recommendations = recommendedInterface.getRecommendations(userId, count);
        List<Long> movieIds = recommendations.stream()
                .map(rec -> ((Number) rec.get(0)).longValue())
                .collect(Collectors.toList());

        return movieService.getMoviesByIds(movieIds);
    }

    public List<Movie> fetchColdRecommendations(int count) {
        List<List<Object>> recommendations = recommendedInterface.getColdRecommendations(count);
        List<Long> movieIds = recommendations.stream()
                .map(rec -> ((Number) rec.get(0)).longValue())
                .collect(Collectors.toList());

        return movieService.getMoviesByIds(movieIds);
    }

}