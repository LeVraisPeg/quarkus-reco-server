package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.*;
import fr.univtln.pegliasco.tp.model.view.RatingId;
import fr.univtln.pegliasco.tp.repository.AccountRepository;
import fr.univtln.pegliasco.tp.repository.MovieRepository;
import fr.univtln.pegliasco.tp.repository.RatingRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class RatingService {
    private static final Logger LOGGER = Logger.getLogger(RatingService.class.getName());

    private final RatingRepository ratingRepository;
    private final AccountRepository accountRepository;
    private final MovieRepository movieRepository;

    @Inject
    public RatingService(RatingRepository ratingRepository, AccountRepository accountRepository, MovieRepository movieRepository) {
        this.ratingRepository = ratingRepository;
        this.accountRepository =  accountRepository;
        this.movieRepository = movieRepository;
    }

    public static File generateCSV(List<RatingId> ratings) throws IOException {
        File tempFile = File.createTempFile("people-", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.append("userId,movieId,rating,timestamp\n");
            for (RatingId p : ratings) {
                writer.append(String.valueOf(p.userId())).append(",")
                        .append(String.valueOf(p.movieId())).append(",")
                        .append(String.valueOf(p.rating())).append(",")
                        .append(String.valueOf(p.timestamp())).append("\n");
            }
        }
        return tempFile;
    }


    public List<RatingId> getAllRatingsId() {
        return ratingRepository.findAllId();
    }


    @Transactional
    public List<Rating> getRatingsPaginated(int page, int size) {
        List<Rating> ratings = ratingRepository.findPaginated(page, size);
        return ratings;
    }


    //addRating
    @Transactional
    public void addRating(Rating rating) {
        ratingRepository.add(rating);
    }





    @Transactional
    public void deleteRating(Long id) {
        ratingRepository.delete(id);
    }

    @Transactional
    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    // Mettre à jour une évaluation par son ID
    @Transactional
    public void updateRating(Long id, Rating rating) {
        Rating existingRating = ratingRepository.findById(id);
        if (existingRating != null) {
            existingRating.setRate(rating.getRate());
            ratingRepository.update(existingRating);
        }
    }

    // Récupérer la note d'un utilisateur pour un film par leurs IDs
    @Transactional
    public List<Rating> getRatingByAccountIdAndMovieId(Long userId, Long movieId) {
        List<Rating> ratings = ratingRepository.findByAccountIdAndMovieId(userId, movieId);
        return ratings.isEmpty() ? List.of() : ratings;
    }


    // get rating by account id
    @Transactional
    public List<Rating> getRatingsByAccountId(Long accountId) {
        return ratingRepository.findByAccountId(accountId);
    }

    //getMovieByRatingId
    @Transactional
    public Movie getMovieByRatingId(Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId);
        if (rating != null) {
            return rating.getMovie();
        }
        return null;
    }

    // ratingService.deleteRating(accountId, movieId);
    @Transactional
    public void deleteRating(Long accountId, Long movieId) {
        Account account = accountRepository.findById(accountId);
        List<Rating> ratings = ratingRepository.findByAccountIdAndMovieId(accountId, movieId);
        LOGGER.info("Suppression des ratings pour accountId=" + accountId + ", movieId=" + movieId + " : " + ratings.size() + " trouvé(s)");
        for (Rating rating : ratings) {
            LOGGER.info("Suppression du rating id=" + rating.getId());
            account.getRatings().remove(rating);
            //ratingRepository.delete(rating);
        }
    }
}
