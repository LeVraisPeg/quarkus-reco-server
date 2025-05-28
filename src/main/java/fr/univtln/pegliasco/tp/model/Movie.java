package fr.univtln.pegliasco.tp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Movie {
    @Id
    private Long id;

    @Column(length = 500)
    private String title;

    @Column
    private Date year;

    @Column
    private Integer runtime;

    @Column
    private String director;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_actors", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor", length = 255)
    private List<String> actors;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_writers", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "writer", length = 255)
    private List<String> writers;


    @Column(length = 1000)
    private String plot;

    @Column(length = 1000)
    private String country;

    @Column(length = 1000)
    private String poster;

    @OneToMany(mappedBy = "movie", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Rating> ratings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Gender> genders;

    @ManyToMany(mappedBy = "movies",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Tag> tags;


}
