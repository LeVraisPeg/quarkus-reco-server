package fr.univtln.pegliasco.tp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Movie {
    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private int year;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Rating> ratings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Gender> genders;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_tag",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnore
    private List<Tag> tags;
}
