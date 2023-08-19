package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;


    public List<Genre> findAll() {
        List<Genre> genreList = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT genre_id, name FROM genre_type");
        while (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getInt("genre_id"))
                    .name(genreRows.getString("name"))
                    .build();
            genreList.add(genre);
        }
        return genreList;
    }

    public void updateGenresForCurrentFilm(Film film) {
        String sqlQuery = "DELETE FROM genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresForCurrentFilm(film);
    }

    public void addGenresForCurrentFilm(Film film) {
        String selectQuery = "SELECT COUNT(*) FROM genre WHERE film_id = ? AND genre_id = ?";
        String insertQuery = "INSERT INTO genre(film_id, genre_id) VALUES (?, ?)";

        Set existingGenres = new HashSet<>();
        List uniqueGenres = new ArrayList<>();
        if (film.getGenres() != null) {
            film.getGenres().forEach(g -> {
                Integer count = jdbcTemplate.queryForObject(selectQuery, Integer.class, film.getId(), g.getId());
                if (count == null || count == 0) {
                    if (!existingGenres.contains(g.getId())) {
                        existingGenres.add(g.getId());
                        uniqueGenres.add(g);
                    }
                }
            });
        }

        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = (Genre) uniqueGenres.get(i);
                ps.setInt(1, film.getId());
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return uniqueGenres.size();
            }
        });
    }

    public Genre getGenreForId(int id) {
        String sqlQuery = "SELECT genre_id, name FROM genre_type WHERE genre_id=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Жанр не найден.");
        }

    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}