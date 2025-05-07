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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany
    @JsonIgnore
    private List<Tag> tags;
}
