package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.Interface.RecommendedInterface;
import fr.univtln.pegliasco.tp.model.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RecommendedService {

    @RestClient
    RecommendedInterface recommendedInterface;

    @Inject
    MovieService movieService;

    public void init() {
        recommendedInterface.initRecommender();
    }

    public List<Movie> fetchRecommendations(Long userId, int count) {
        List<Long> movieIds = recommendedInterface.getRecommendations(userId, count);
        return movieService.getMoviesByIds(movieIds);
    }

    public List<Movie> fetchColdRecommendations(int count) {
        List<Long> movieIds = recommendedInterface.getColdRecommendations(count);
        return movieService.getMoviesByIds(movieIds);
    }



}