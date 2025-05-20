package fr.univtln.pegliasco.tp.model.nosql;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import fr.univtln.pegliasco.tp.model.nosql.MovieElastic;
import fr.univtln.pegliasco.tp.model.nosql.MovieElasticService;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;

public class TestElasticSearch {

    public static void main(String[] args) throws IOException {
        // Connexion à Elasticsearch
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchClient client = new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );

        MovieElasticService service = new MovieElasticService(client);

        // Appel de la méthode de recherche
        List<MovieElastic> movies = service.searchByTitle("Romance");

        // Affichage des résultats
        for (MovieElastic movie : movies) {
            System.out.println("Movie found: " + movie.getTitle());
        }

        restClient.close();
    }
}

