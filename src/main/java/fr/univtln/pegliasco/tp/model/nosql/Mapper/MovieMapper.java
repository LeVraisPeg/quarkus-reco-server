package fr.univtln.pegliasco.tp.model.nosql.Mapper;

import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.nosql.Elastic.MovieElastic;

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

    public static Movie fromElastic(MovieElastic movieElastic) {
        Movie movie = new Movie();
        movie.setId(movieElastic.getId());
        movie.setTitle(movieElastic.getTitle());
        movie.setYear(movieElastic.getYear() != 0 ? java.util.Date.from(java.time.LocalDate.of(movieElastic.getYear(), 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()) : null);
        movie.setDirector(movieElastic.getDirector());
        movie.setWriters(movieElastic.getWriters());
        movie.setActors(movieElastic.getActors());
        movie.setPlot(movieElastic.getPlot());
        movie.setCountry(movieElastic.getCountry());
        movie.setRuntime(movieElastic.getRuntime() != 0 ? movieElastic.getRuntime() : null);

        return movie;
    }
}
