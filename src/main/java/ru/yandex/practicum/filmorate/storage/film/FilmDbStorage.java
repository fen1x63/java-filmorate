package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;
import ru.yandex.practicum.filmorate.service.film.MpaService;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final LikeStorage likeDbStorage;
    private final GenreService genreService;

    @Override
    public List<Film> findAllFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT film_id, name, description, release_date," +
                " duration, rating_mpa_id FROM films");
        while (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("film_id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(mpaService.getMpa(filmRows.getInt("rating_mpa_id")))
                    .build();
            film.setGenres(getGenreForCurrentFilm(film.getId()));
            film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));

            films.add(film);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        mpaService.addMpaToFilm(film);
        genreService.addGenresForCurrentFilm(film);
        genreService.addGenreNameToFilm(film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name=?, description=?, release_date=?, duration=?, rating_mpa_id=? WHERE film_id=?";
        int rowsCount = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (rowsCount > 0) {
            mpaService.addMpaToFilm(film);
            genreService.updateGenresForCurrentFilm(film);
            genreService.addGenreNameToFilm(film);
            film.setGenres(getGenreForCurrentFilm(film.getId()));
            return film;
        }
        else {
            throw new EntityNotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, rating_mpa_id " +
                "FROM films WHERE film_id=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film like(int filmId, int userId) {
        Film film = getFilmById(filmId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public List<Film> getRating(int count) {
        String sqlQuery = "SELECT films.*, COUNT(l.film_id) as count FROM films\n" +
                "LEFT JOIN likes l ON films.film_id=l.film_id\n" +
                "GROUP BY films.film_id\n" +
                "ORDER BY count DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaService.getMpa(resultSet.getInt("rating_mpa_id")))
                .build();
        film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
        film.setGenres(getGenreForCurrentFilm(film.getId()));
        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_mpa_id", film.getMpa().getId());
        return values;
    }

    public Set<Genre> getGenreForCurrentFilm(int id) {
        Set<Genre> genreSet = new LinkedHashSet<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT id, film_id, genre_id FROM genre " +
                "WHERE film_id = ? GROUP BY id ORDER BY genre_id ASC", id);
        while (genreRows.next()) {
            genreSet.add(genreService.getGenreForId(genreRows.getInt("genre_id")));
        }
        return genreSet;
    }
}
