package fr.univtln.pegliasco.tp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User extends Account {

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;


}
