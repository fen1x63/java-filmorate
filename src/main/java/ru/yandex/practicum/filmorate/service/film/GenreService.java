package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GenreService {

    private final GenreStorage genreDbStorage;

    public GenreService(@Lazy GenreStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public void addGenresForCurrentFilm(Film film) {
        if (film.getGenres() != null) {
            genreDbStorage.updateGenresForCurrentFilm(film);
        }
    }

    public void addGenreNameToFilm(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }
        film.getGenres().forEach(g -> g.setName(getGenreForId(g.getId()).getName()));
    }

    public Genre getGenreForId(int id) {
        return genreDbStorage.getGenreForId(id);
    }

    public Genre getGenre(int genreId) {
        return genreDbStorage.getGenreForId(genreId);
    }

}
