package fr.univtln.pegliasco.tp.model.nosql;

import fr.univtln.pegliasco.tp.model.Movie;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class MovieMapper {

    public static MovieElastic toElastic(Movie movie) {
        int year = 0;
        if (movie.getYear() != null) {
            year = movie.getYear().toInstant().atZone(ZoneId.systemDefault()).getYear();
        }

        List<String> genders = movie.getGenders().stream()
                .map(g -> g.getName())
                .collect(Collectors.toList());

        return new MovieElastic(
                String.valueOf(movie.getId()),
                movie.getTitle(),
                year,
                movie.getDirector(),
                movie.getPlot(),
                movie.getCountry(),
                movie.getRuntime(),
                genders
        );
    }
}
