package fr.univtln.pegliasco.tp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {
    @Id
    private Long id;

    @Column
    private String nom;

    @Column
    private String prenom;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    public Role role;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Rating> ratings;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<RatingCache> ratingscache;

    public enum Role {
        USER,
        ADMIN
    }



}
