package fr.univtln.pegliasco.tp.model.nosql;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ElasticsearchClientProducer {

    @Inject
    @ConfigProperty(name = "elasticsearch.host", defaultValue = "localhost")
    String host;

    @Inject
    @ConfigProperty(name = "elasticsearch.port", defaultValue = "9200")
    int port;

    @Produces
    public ElasticsearchClient createClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port)).build();

        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
}