package fr.univtln.pegliasco.tp.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.GenderElastic;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.MovieElastic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class GenderElasticService {

    private static final String INDEX = "genders";
    private final ElasticsearchClient client;
    private final MovieElasticService movieElasticService;

    @Inject
    public GenderElasticService(ElasticsearchClient client, MovieElasticService movieElasticService) {
        this.client = client;
        this.movieElasticService = movieElasticService;
    }

    public void ping() throws IOException {
        // Effectue une requête info pour vérifier la connexion
        client.info();
    }

    // Indexer un genre
    public void indexGender(GenderElastic genderElastic) throws IOException {
        client.index(i -> i
                .index(INDEX)
                .id(String.valueOf(genderElastic.getId()))
                .document(genderElastic)
        );
    }

    //getAllGenders
    public List<GenderElastic> getAllGenders() throws IOException {
        var response = client.search(s -> s
                .index(INDEX)
                .query(q -> q
                        .matchAll(m -> m)
                ), GenderElastic.class);
        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }


    public List<MovieElastic> getMoviesByGender(String name) throws IOException {
        var response = client.search(s -> s
                .index(INDEX)
                .query(q -> q
                        .match(m -> m
                                .field("name")
                                .query(name)
                                .fuzziness("AUTO")
                        )
                ), GenderElastic.class);

        // Récupère tous les IDs de films pour ce genre
        List<Long> movieIds = response.hits().hits().stream()
                .map(hit -> hit.source())
                .filter(Objects::nonNull)
                .flatMap(genderElastic -> genderElastic.getMovies() != null ? genderElastic.getMovies().stream() : java.util.stream.Stream.empty())
                .toList();

        // Utilise MovieService ou MovieRepository pour charger les entités Movie
        // Supposons que tu as un MovieService injecté :
        // @Inject MovieService movieService;
        return movieIds.isEmpty() ? List.of() : movieElasticService.getMoviesByIds(movieIds);
    }
}
