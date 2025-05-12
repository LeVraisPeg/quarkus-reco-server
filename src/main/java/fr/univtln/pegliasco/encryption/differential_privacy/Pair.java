package fr.univtln.pegliasco.encryption.differential_privacy;

import com.google.auto.value.AutoValue.Builder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Pair<T, U> {
    private T first;
    private U second;
}
