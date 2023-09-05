package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MOST_POPULAR_QUANTITY = 10;
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public FilmService(@Qualifier("DbFilmStorage")
                       FilmStorage filmStorage, @Qualifier("DbUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilmById(Integer id) {
        log.info(String.format("FilmService: Поиск фильма по идентификатору %d", id));
        Film film = filmStorage.getFilm(id);
        if (film == null)
            throw new NotFoundException(String.format("Фильм %d не найден!", id));
        return film;
    }

    public List<Film> getAllFilms() {
        log.info("FilmService: Получение списка всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        log.info("FilmService: Добавление фильма");
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info(String.format("FilmService: Изменение данных фильма по идентификатору %d", film.getId()));
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film addLike(Integer id, Integer userId) {
        log.info(String.format("FilmService: Добавление лайка фильму %d пользователем %d", id, userId));
        User user = userStorage.getUser(userId);
        if (user == null)
            throw new NotFoundException(String.format("Пользователь %d не найден!", userId));
        return filmStorage.addLike(id, userId);
    }

    public Film deleteLike(Integer id, Integer userId) {
        log.info(String.format("FilmService: Удаление лайка фильму %d пользователем %d", id, userId));
        User user = userStorage.getUser(userId);
        if (user == null)
            throw new NotFoundException(String.format("Пользователь %d не найден!", id));
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> getMostPopular(Integer count) {
        int filmCount = (count != null && count > 0) ? count : MOST_POPULAR_QUANTITY;
        log.info(String.format("FilmService: Вывод %d наиболее популярных фильмов", filmCount));
        return filmStorage.getMostPopular(filmCount);
    }

    public void validateFilm(Film film) {
        if (!StringUtils.hasText(film.getName())) {
            String emptyNameMessage = "Название не должно быть пустым!";
            log.error(emptyNameMessage);
            throw new ValidationException(emptyNameMessage);
        }
        int maxDescriptionLength = 200;
        if (film.getDescription() != null && film.getDescription().length() > maxDescriptionLength) {
            String errorMessage = String.format("Описание не должно быть длиннее %d символов!", maxDescriptionLength);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String tooOldMessage = "Дата релиза не может быть ранее %s!";
            String errorMessage = String.format(tooOldMessage, MIN_RELEASE_DATE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getDuration() <= 0) {
            String negativeDurationMessage = "Продолжительность фильма должна быть положительной!";
            log.error(negativeDurationMessage);
            throw new ValidationException(negativeDurationMessage);
        }
    }
}
