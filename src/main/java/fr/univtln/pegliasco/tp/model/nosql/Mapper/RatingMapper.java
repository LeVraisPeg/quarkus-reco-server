package fr.univtln.pegliasco.tp.model.nosql.Mapper;

import fr.univtln.pegliasco.tp.model.nosql.Elastic.RatingElastic;
import fr.univtln.pegliasco.tp.model.Rating;

import java.time.ZoneId;
import java.util.stream.Collectors;
import java.util.List;


public class RatingMapper {
    public static RatingElastic toElastic(Rating rating) {

        return new RatingElastic(
                rating.getId(),
                rating.getRate(),
                rating.getTimestamp(),
                rating.getMovie() != null ? rating.getMovie().getId() : null,
                rating.getAccount() != null ? rating.getAccount().getId() : null
        );
    }


}
