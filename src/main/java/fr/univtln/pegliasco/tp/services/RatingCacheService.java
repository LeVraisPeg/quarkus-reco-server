package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Account;
import fr.univtln.pegliasco.tp.model.RatingCache;
import fr.univtln.pegliasco.tp.repository.RatingCacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

import java.util.logging.Logger;


@ApplicationScoped
public class RatingCacheService {
    private static final Logger LOGGER = Logger.getLogger(RatingService.class.getName());
    private final RatingCacheRepository ratingCacheRepository;
    private final AccountService accountService;

    public RatingCacheService(RatingCacheRepository ratingCacheRepository, AccountService accountService) {
        this.ratingCacheRepository = ratingCacheRepository;
        this.accountService = accountService;
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

    @Transactional
    public void updateRatingInCache(RatingCache ratingCache) {
        RatingCache existingRating = ratingCacheRepository.findById(ratingCache.getId());
        if (existingRating != null) {
            existingRating.setRate(ratingCache.getRate());
            ratingCacheRepository.update(existingRating);
        }
    }

    @Transactional
    public void deleteRatingFromCache(Long accountId, Long movieId) {
        Account account = accountService.findById(accountId);
        List<RatingCache> ratings = ratingCacheRepository.findByAccountId(accountId);
        LOGGER.info("Deleting rating from cache for accountId: " + accountId + " and movieId: " + movieId);
        for (RatingCache rating : ratings) {
            if (rating.getMovie().getId().equals(movieId)) {
                LOGGER.info("Found rating in cache for accountId: " + accountId + " and movieId: " + movieId);
                account.getRatingscache().remove(rating);
                //ratingCacheRepository.delete(rating);
                break;
            }
        }
    }
}
