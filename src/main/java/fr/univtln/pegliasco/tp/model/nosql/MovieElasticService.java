package fr.univtln.pegliasco.tp.model.nosql;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import fr.univtln.pegliasco.tp.model.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MovieElasticService {

    private static final String INDEX = "movies";

    private final ElasticsearchClient client;

     @Inject
    public MovieElasticService(ElasticsearchClient client) {
        this.client = client;
    }
    public void ping() throws IOException {
        // Effectue une requête info pour vérifier la connexion
        client.info();
    }

    // Indexer un film
    public void indexMovie(MovieElastic movieElastic) throws IOException {
        client.index(i -> i
                .index(INDEX)
                .id(movieElastic.getId())
                .document(movieElastic)
        );
    }

    public List<Movie> searchMovies(String keyword) throws IOException {
        var response = client.search(s -> s
                .index("movies")
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(keyword)
                        )), Movie.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }





    // Récupérer un film par id
    public MovieElastic getMovieById(String id) throws IOException {
        GetResponse<MovieElastic> response = client.get(g -> g
                .index(INDEX)
                .id(id), MovieElastic.class);

        if (response.found()) {
            return response.source();
        }
        return null;
    }

    // Recherche par titre (match)
    public List<MovieElastic> searchByTitle(String title) throws IOException {
        SearchResponse<MovieElastic> response = client.search(s -> s
                .index(INDEX)
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(title)
                        )
                ), MovieElastic.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }


}


