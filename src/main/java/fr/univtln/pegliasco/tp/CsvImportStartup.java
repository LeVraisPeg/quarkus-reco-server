package fr.univtln.pegliasco.tp;

import fr.univtln.pegliasco.tp.services.AccountService;
import fr.univtln.pegliasco.tp.services.CsvImporterService;
import fr.univtln.pegliasco.tp.services.MovieElasticService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;

import static com.arjuna.ats.jta.logging.jtaLogger.logger;

@Startup
@ApplicationScoped
public class CsvImportStartup {

    @Inject
    CsvImporterService csvImporterService;
    @Inject
    MovieElasticService movieElasticService;

    @PostConstruct
    public void onStart() {

        Logger logger = Logger.getLogger(AccountService.class.getName());
        try {
            logger.info("▶ Import CSV on startup...");
            waitForElasticsearchReady();

            try (InputStream movieStream = getClass().getClassLoader().getResourceAsStream("Data/movies_created.csv")) {
                csvImporterService.importMoviesFromCsv(movieStream);
            }

            try (InputStream ratingStream = getClass().getClassLoader().getResourceAsStream("Data/ratings.csv")) {
                csvImporterService.importRatingsFromCsv(ratingStream);
            }
            try (InputStream tagStream = getClass().getClassLoader().getResourceAsStream("Data/tags.csv")) {
                csvImporterService.importTagsFromCsv(tagStream);
            }

            logger.info("✅ Import completed successfully.");

        } catch (Exception e) {
            logger.info(String.format("❌ Error during CSV import: %s", e.getMessage()));
        }
    }

    private String getResourceFilePath(String fileName) throws IOException {
        return new File(getClass().getClassLoader().getResource(fileName).getFile()).getAbsolutePath();
    }

    private void waitForElasticsearchReady() {
        int maxRetries = 30;
        int retry = 0;
        while (retry < maxRetries) {
            try {
                movieElasticService.ping();
                logger.info("Elasticsearch est prêt.");
                return;
            } catch (Exception e) {
                retry++;
                logger.warnf("Elasticsearch non prêt, tentative %d/%d", retry, maxRetries);
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Elasticsearch n'est pas prêt après plusieurs tentatives.");
    }
}