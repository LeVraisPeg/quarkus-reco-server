package fr.univtln.pegliasco.tp.services;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;

import fr.univtln.pegliasco.tp.model.nosql.Elastic.MovieElastic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
                .id(String.valueOf(movieElastic.getId()))
                .document(movieElastic)
        );
    }


    public List<MovieElastic> searchMovies(String keyword) throws IOException {
        var response = client.search(s -> s
                .index("movies")
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(keyword)
                                .fuzziness("AUTO")
                        )
                ), MovieElastic.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }





    // Récupérer un film par id
    public MovieElastic getMovieById(Long id) throws IOException {
        GetResponse<MovieElastic> response = client.get(g -> g
                .index(INDEX)
                .id(String.valueOf(id)), MovieElastic.class);

        if (response.found()) {
            return response.source();
        }
        return null;
    }

    //Recupérer un film par genre
    public List<MovieElastic> getMoviesByGender(String gender) throws IOException {
        var response = client.search(s -> s
                .index(INDEX)
                .query(q -> q
                        .term(t -> t
                                .field("genders")
                                .value(v -> v.stringValue(gender))
                        )
                ), MovieElastic.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }

    //getMoviesByIds

    // Récupérer plusieurs films par leurs IDs
    public List<MovieElastic> getMoviesByIds(List<Long> ids) throws IOException {
        var response = client.mget(m -> m
                .index(INDEX)
                .ids(ids.stream().map(String::valueOf).toList()), MovieElastic.class);

        return response.docs().stream()
                .map(item -> item.result() != null && item.result().found() ? item.result().source() : null)
                .filter(Objects::nonNull)
                .toList();
    }


}


