package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> findAll();

    void updateGenresForCurrentFilm(Film film);

    Genre getGenreForId(int id);
}
