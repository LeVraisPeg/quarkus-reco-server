package fr.univtln.pegliasco.tp.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;

import fr.univtln.pegliasco.tp.model.nosql.Elastic.RatingElastic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class RatingElasticService {

    private static final String INDEX = "ratings";

    private final ElasticsearchClient client;

    @Inject
    public RatingElasticService(ElasticsearchClient client) {
        this.client = client;
    }

    public void ping() throws IOException {
        // Effectue une requête info pour vérifier la connexion
        client.info();
    }

    // Indexer un film
    public void indexRating(RatingElastic ratingElastic) throws IOException {
        client.index(i -> i
                .index(INDEX)
                .id(String.valueOf(ratingElastic.getId()))
                .document(ratingElastic)
        );
    }

    public RatingElastic getRatingById(Long id) throws IOException {
        GetResponse<RatingElastic> response = client.get(g -> g
                .index(INDEX)
                .id(String.valueOf(id)), RatingElastic.class);

        if (response.found()) {
            return response.source();
        } else {
            return null;
        }
    }
}
