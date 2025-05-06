package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Rating;
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
        return ratingRepository.findAll();
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

    @Transactional
    public List<Rating> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId);
    }


    // Récupérer la note d'un utilisateur pour un film par leurs IDs
    @Transactional
    public Rating getRatingByUserIdAndMovieId(Long userId, Long movieId) {
        return ratingRepository.findByUserIdAndMovieId(userId, movieId);
    }



}
