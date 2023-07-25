package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = "application/json")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на добавление фильма");
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на изменения фильма.");
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{filmId}")
    public void like(@PathVariable Integer id, @PathVariable Integer filmId) {
        log.info("Поступил запрос на присвоение лайка фильму.");
        filmService.like(id, filmId);
    }

    @GetMapping()
    public List<Film> getFilms() {
        log.info("Поступил запрос на получение списка всех фильмов.");
        return filmStorage.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Получен GET-запрос на получение фильма");
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Поступил запрос на получение списка популярных фильмов.");
        return filmService.getTopFilms(count);
    }

    @DeleteMapping("/{id}/like/{filmId}")
    public void deleteLike(@PathVariable @Positive Integer id, @PathVariable Integer filmId) {
        filmService.checkId(filmId);
        log.info("Поступил запрос на удаление лайка у фильма.");
    }
}
