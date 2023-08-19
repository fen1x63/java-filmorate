package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            log.info("Фильм изменён.");
        } else {
            throw new EntityNotFoundException("Фильм не найден.");
        }
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new EntityNotFoundException("Фильм не найден.");
        }
    }

    private int getIdForFilm() {
        return ++idForFilm;
    }

    private void filmValidation(Film film) {
        checkId(film.getId());
        Set<Genre> set = new HashSet<>();
        if (film.getGenres() != null) {
            for (Genre element : film.getGenres()) {
                if (!set.contains(element)) {
                    set.add(element);
                }
            }
        }
        film.setGenres(set);
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

    public static void checkId(Integer id) {
        if (id < 0) {
            throw new EntityNotFoundException("Id не может быть отрицательным");
        }
    }

    public Film deleteLike(int userId, int filmId) {
        Film film = getFilmById(filmId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            throw new EntityNotFoundException("Пользователь не ставил лайк этому фильму.");
        }
        return film;
    }

    @Override
    public Film like(int filmId, int userId) {
        getFilmById(filmId).getLikes().add(userId);
        return getFilmById(filmId);
    }

    public List<Film> getRating(int count) {
        return findAllFilms()
                .stream()
                .sorted((film1, film2) ->
                        film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}