package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.User;
import fr.univtln.pegliasco.tp.model.view.RatingId;
import fr.univtln.pegliasco.tp.repository.RatingRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
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

    @Transactional
    public List<Rating> getAllRatings() {
        List<Rating> ratings = ratingRepository.findAll();
        ratings.forEach(rating -> {
            if (rating.getAccount() instanceof User user && user.getRatings() != null) {
                user.getRatings().size();
            }
        });
        return ratings;
    }

    public List<RatingId> getAllRatingsId() {
        return ratingRepository.findAllId();
    }

    @Transactional
    public List<Rating> getRatingsPaginated(int page, int size) {
        List<Rating> ratings = ratingRepository.findPaginated(page, size);

        // Force l'initialisation des collections liées (si besoin)
        ratings.forEach(rating -> {
            if (rating.getAccount() instanceof User user && user.getRatings() != null) {
                user.getRatings().size();
            }
        });

        return ratings;
    }

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
