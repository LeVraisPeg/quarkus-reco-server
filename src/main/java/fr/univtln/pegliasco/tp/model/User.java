package fr.univtln.pegliasco.tp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User extends Account {

    @OneToMany(mappedBy = "account")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "account")
    private List<Tag> tags;

}
