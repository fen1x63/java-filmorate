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
        String deleteQuery = "DELETE FROM genre WHERE film_id = ?";
        String insertQuery = "INSERT IGNORE INTO genre(film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.update(deleteQuery, film.getId());

        Set<Integer> existingGenres = new HashSet<>();
        List<Genre> uniqueGenres = new ArrayList<>();
        if (film.getGenres() != null) {
            film.getGenres().forEach(g -> {
                if (!existingGenres.contains(g.getId())) {
                    existingGenres.add(g.getId());
                    uniqueGenres.add(g);
                }
            });
        }

        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = uniqueGenres.get(i);
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