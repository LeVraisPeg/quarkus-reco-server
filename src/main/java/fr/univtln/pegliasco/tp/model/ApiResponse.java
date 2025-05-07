package fr.univtln.pegliasco.tp.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    public String message;

    public ApiResponse(String message) {
        this.message = message;
    }
}