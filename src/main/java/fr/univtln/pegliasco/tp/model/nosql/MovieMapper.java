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

        List<String> genders = movie.getGenders() != null
                ? movie.getGenders().stream().map(g -> g.getName()).collect(Collectors.toList())
                : null;

        List<String> tags = movie.getTags() != null
                ? movie.getTags().stream().map(t -> t.getName()).collect(Collectors.toList())
                : null;

        return new MovieElastic(
                movie.getId(),
                movie.getTitle(),
                year,
                movie.getDirector(),
                movie.getWriters(),
                movie.getActors(),
                movie.getPlot(),
                movie.getCountry(),
                movie.getRuntime() != null ? movie.getRuntime() : 0,
                genders,
                movie.getPoster(),
                tags
        );
    }
}
