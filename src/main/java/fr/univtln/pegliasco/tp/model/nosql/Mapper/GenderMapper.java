package fr.univtln.pegliasco.tp.model.nosql.Mapper;

import fr.univtln.pegliasco.tp.model.Gender;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.GenderElastic;

import java.util.Collections;
import java.util.List;




public class GenderMapper {
    public static GenderElastic toElastic(Gender gender) {
        List<Long> movieIds = gender.getMovies() != null
                ? gender.getMovies().stream().map(Movie::getId).toList()
                : Collections.emptyList();
        return new GenderElastic(
                gender.getId(),
                gender.getName(),
                movieIds
        );
    }
}
