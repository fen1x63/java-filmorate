package ru.yandex.practicum.filmorate.service.film;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public void like(int filmId, int userId) {
        filmStorage.getFilmById(filmId).getLikes().add(userId);
    }

    public void checkId(Integer id) {
        if (id < 0) {
            throw new EntityNotFoundException("Id не может быть отрицательным");
        }
    }

    public void deleteLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            throw new EntityNotFoundException("Пользователь не ставил лайк этому фильму.");
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findAllFilms()
                .stream()
                .sorted((film1, film2) ->
                        film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}
