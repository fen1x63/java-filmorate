package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film like(Integer filmId, Integer userId) {
        Film film = filmStorage.like(filmId, userId);
        return film;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден.");
        }
        Film film = filmStorage.deleteLike(filmId, userId);
        return film;
    }

    public List<Film> getBestFilmsOfGenreAndYear(int count, int genre, int year) {
        return filmStorage.getPopularFilms(count, genre, year);
    }

    public Film deleteFilm(Integer id) {
        return filmStorage.deleteFilm(id);
    }
}
