package fr.univtln.pegliasco.tp;

import fr.univtln.pegliasco.tp.services.AccountService;
import fr.univtln.pegliasco.tp.services.CsvImporterService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;

@Startup
@ApplicationScoped
public class CsvImportStartup {

    @Inject
    CsvImporterService csvImporterService;

    @PostConstruct
    public void onStart() {
        Logger logger = Logger.getLogger(AccountService.class.getName());
        try {
            logger.info("▶ Import CSV on startup...");

            // Example file paths in /resources
            String moviePath = getResourceFilePath("Data/movies.csv");
            String ratingPath = getResourceFilePath("Data/ratings.csv");
            String tagPath = getResourceFilePath("Data/tags.csv");

            csvImporterService.importMoviesFromCsv(moviePath);
            csvImporterService.importRatingsFromCsv(ratingPath);
            csvImporterService.importTagsFromCsv(tagPath);
            logger.info("✅ Import completed successfully.");

        } catch (Exception e) {
            logger.info(String.format("❌ Error during CSV import: %s", e.getMessage()));
        }
    }

    private String getResourceFilePath(String fileName) throws IOException {
        return new File(getClass().getClassLoader().getResource(fileName).getFile()).getAbsolutePath();
    }
}