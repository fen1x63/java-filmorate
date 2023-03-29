package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DataObjectService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController extends AbstractDataObjectController<Film> {

    @Override
    protected DataObjectService<Film> getService() {
        return new FilmService();
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        return super.create(film);
    }

    @Override
    @GetMapping
    public ResponseEntity<Collection<Film>> read() {
        return super.read();
    }

    @Override
    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        return super.update(film);
    }

}
