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

@ApplicationScoped
public class RatingService {
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
            writer.append("userId,movieId,rating\n");
            for (RatingId p : ratings) {
                writer.append(String.valueOf(p.userId())).append(",")
                        .append(String.valueOf(p.movieId())).append(",")
                        .append(String.valueOf(p.rating())).append("\n");
            }
        }
        return tempFile;
    }


    public List<RatingId> getAllRatingsId() {
        return ratingRepository.findAllId();
    }


    public RatingCache getRatingCacheById(Long id) {
        return ratingRepository.findCacheById(id);
    }

    @Transactional
    public List<Rating> getRatingsPaginated(int page, int size) {
        List<Rating> ratings = ratingRepository.findPaginated(page, size);
        return ratings;
    }

    @Transactional
    public List<RatingCache> getRatingsPaginatedFromCache(int page, int size) {
        List<RatingCache> ratings = ratingRepository.findPaginatedFromCache(page, size);
        return ratings;
    }

    //addRating
    @Transactional
    public void addRating(Rating rating) {
        ratingRepository.add(rating);
    }

    //addRatingToCache
    // Dans RatingService.java

    @Transactional
    public void addRatingToCache(Float rate, Long accountId, Long movieId) {
        RatingCache rating = new RatingCache();
        rating.setRate(rate);
        Account account = accountRepository.findById(accountId);
        Movie movie = movieRepository.findById(movieId);
        rating.setAccount(account);
        rating.setMovie(movie);
        ratingRepository.addCache(rating);
    }

    //deleteRatingToCache
    @Transactional
    public void deleteRatingtoCache(Long id) {
        ratingRepository.deleteCache(id);
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

    //updateRatingToCache
    @Transactional
    public void updateRatingToCache(Long id, RatingCache rating) {
        RatingCache existingRatingCache = ratingRepository.findCacheById(id);
        if (existingRatingCache != null) {
            existingRatingCache.setRate(rating.getRate());
            ratingRepository.updateCache(existingRatingCache);
        }
    }

    // Récupérer la note d'un utilisateur pour un film par leurs IDs
    @Transactional
    public List<Rating> getRatingByAccountIdAndMovieId(Long userId, Long movieId) {
        List<Rating> ratings = ratingRepository.findByAccountIdAndMovieId(userId, movieId);
        return ratings.isEmpty() ? List.of() : ratings;
    }

    // save or update
    @Transactional
    public void saveOrUpdate(Rating rating) {
        if (rating.getId() == null) {
            ratingRepository.add(rating);
        } else {
            ratingRepository.update(rating);
        }
    }

    // get rating by account id
    @Transactional
    public List<Rating> getRatingsByAccountId(Long accountId) {
        return ratingRepository.findByAccountId(accountId);
    }

}
