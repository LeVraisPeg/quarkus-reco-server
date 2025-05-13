package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.User;
import fr.univtln.pegliasco.tp.repository.RatingRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
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
    public Rating getRatingByAccountIdAndMovieId(Long userId, Long movieId) {
        return ratingRepository.findByAccountIdAndMovieId(userId, movieId);
    }


    //save or update
    @Transactional
    public void saveOrUpdate(Rating rating) {
        if (rating.getId() == null) {
            ratingRepository.add(rating);
        } else {
            ratingRepository.update(rating);
        }
    }

    //get rating by account id
    @Transactional
    public List<Rating> getRatingsByAccountId(Long accountId) {
        return ratingRepository.findByAccountId(accountId);
    }


}
