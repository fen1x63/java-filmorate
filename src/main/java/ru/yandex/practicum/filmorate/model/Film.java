package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Getter
@Setter
@Builder
public class Film {

    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    @NotNull
    private String description;

    @NotNull
    @ReleaseDate
    private LocalDate releaseDate;

    @Positive
    private long duration;

    private LinkedHashSet<Genre> genres;

    @NotNull
    private Mpa mpa;

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
