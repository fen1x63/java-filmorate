package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
public class Film {

    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @ReleaseDate(message = "Некорректна указана дата релиза.")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @EqualsAndHashCode.Exclude
    private Set<Integer> likes;
    @EqualsAndHashCode.Exclude
    private Set<Genre> genres;
    @NotNull
    @EqualsAndHashCode.Exclude
    private Mpa mpa;

}