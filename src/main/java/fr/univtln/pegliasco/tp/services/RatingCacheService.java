package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.RatingCache;
import fr.univtln.pegliasco.tp.repository.RatingCacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;


@ApplicationScoped
public class RatingCacheService {
    private final RatingCacheRepository ratingCacheRepository;

    public RatingCacheService(RatingCacheRepository ratingCacheRepository) {
        this.ratingCacheRepository = ratingCacheRepository;
    }


    //getRatingsPaginatedFromCache
    public RatingCache getRatingCacheById(Long id) {
        return ratingCacheRepository.findById(id);
    }


    public List<RatingCache> getRatingsPaginatedFromCache(int page, int size) {
        return ratingCacheRepository.findPaginated(page, size);
    }

    @Transactional
    public void addRatingToCache(RatingCache ratingCache) {
        ratingCacheRepository.add(ratingCache);
    }


    @Transactional
    public void deleteRatingFromCache(Long id) {
        RatingCache ratingCache = ratingCacheRepository.findById(id);
        if (ratingCache != null) {
            ratingCacheRepository.delete(ratingCache);
        }
    }


    public List<RatingCache> getRatingCacheByAccountId(Long accountId) {
        return ratingCacheRepository.findByAccountId(accountId);
    }


    public void updateRatingInCache(RatingCache ratingCache) {
        RatingCache existingRating = ratingCacheRepository.findById(ratingCache.getId());
        if (existingRating != null) {
            existingRating.setRate(ratingCache.getRate());
            ratingCacheRepository.update(existingRating);
        }
    }
}
