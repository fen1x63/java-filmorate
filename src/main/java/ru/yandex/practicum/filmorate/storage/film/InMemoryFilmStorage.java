package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate UNCORRECTDATE = LocalDate.of(1895, 12, 28);
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int idForFilm = 0;

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        filmValidation(film);
        film.setLikes(new HashSet<>());
        film.setId(getIdForFilm());
        films.put(film.getId(), film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.get(film.getId()) != null) {
            filmValidation(film);
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            log.info("Поступил запрос на изменения фильма. Фильм изменён.");
        } else {
            log.error("Поступил запрос на изменения фильма. Фильм не найден.");
            throw new EntityNotFoundException("Film not found.");
        }
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new EntityNotFoundException("Film not found.");
        }
    }

    private int getIdForFilm() {
        return ++idForFilm;
    }

    private void filmValidation(Film film) {
        if (film.getReleaseDate().isBefore(UNCORRECTDATE)
                || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректно указана дата релиза.");
        }
        if (film.getName().isEmpty()) {
            throw new ValidationException("Некорректно указано название фильма.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышено количество символов в описании фильма.");
        }
    }

}