package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = "application/json")
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Получен GET-запрос на получение фильма");
        return filmService.getFilmById(id);
    }

    @GetMapping()
    public List<Film> getFilms() {
        log.info("Поступил запрос на получение списка всех фильмов.");
        return filmService.findAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на добавление фильма.");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на изменения фильма.");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film like(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.info("Поступил запрос на присвоение лайка фильму.");
        return filmService.like(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.info("Поступил запрос на удаление лайка у фильма.");
        return filmService.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Integer id) {
        log.info("Поступил запрос на удаление фильма с id: " + id);
        return filmService.deleteFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilmsOfGenreAndYear(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "-1") int genreId,
            @RequestParam(defaultValue = "-1") int year) {
        log.info("Поступил запрос на получение списка популярных фильмов по годам и жанрам.");
        log.info("count {}, genre {} , year {}", count, genreId, year);
        return filmService.getBestFilmsOfGenreAndYear(count, genreId, year);
    }

}
