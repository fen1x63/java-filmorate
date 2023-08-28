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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film getFilmById(Integer id) {
        final String getFilmSqlQuery =
                "SELECT films.* " +
                        "FROM films " +
                        "WHERE films.film_id = ?";
        try {
            return jdbcTemplate.queryForObject(getFilmSqlQuery, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Фильм не найден.");
        }
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "SELECT films.*, mpa_type.mpa_name FROM films JOIN mpa_type ON films.rating_mpa_id =" +
                " mpa_type.rating_mpa_id";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        addMpa(film);
        addGenreName(film);
        addGenresForCurrentFilm(film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен.");
        return film;
    }

    @Override
    public Film deleteFilm(Integer id) {
        Film film = getFilmById(id);
        String sqlQuery =
                "DELETE " +
                        "FROM films " +
                        "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery =
                "UPDATE films " +
                        "SET name=?, description=?, release_date=?, duration=?, rating_mpa_id=?" +
                        "WHERE film_id=?";

        int rowsCount;
        rowsCount = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        addMpa(film);
        updateGenres(film);
        addGenreName(film);
        film.setGenres(getGenre(film.getId()));

        if (rowsCount > 0) {
            return getFilmById(film.getId());
        } else {
            throw new EntityNotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film like(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        String sqlQuery =
                "INSERT " +
                        "INTO likes (film_id, user_id) " +
                        "VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        String sqlQuery =
                "DELETE " +
                        "FROM likes " +
                        "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count, int genre, int year) {
        String sqlQuery;
        String sqlQueryStart =
                "SELECT films.*, COUNT(l.film_id) as count, G.*, GT.*, GT.NAME as genre_name " +
                        "FROM films " +
                        "LEFT JOIN likes l ON films.film_id=l.film_id " +
                        "LEFT JOIN GENRE G on FILMS.FILM_ID = G.FILM_ID " +
                        "LEFT JOIN GENRE_TYPE GT on G.ID = GT.GENRE_ID ";

        String sqlQueryFinish = "GROUP BY films.film_id, gt.NAME " +
                "ORDER BY count DESC " +
                "LIMIT ?";

        if (year == -1 && genre == -1) {
            String popularFilmsSqlQuery =
                    "SELECT films.*, COUNT(l.film_id) as count " +
                            "FROM films " +
                            "LEFT JOIN likes l ON films.film_id=l.film_id " +
                            "GROUP BY films.film_id " +
                            "ORDER BY count DESC " +
                            "LIMIT ?";

            return jdbcTemplate.query(popularFilmsSqlQuery, (resultSet, rowNum) -> Film.builder()
                    .id(resultSet.getInt("film_id"))
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .releaseDate(Objects.requireNonNull(resultSet.getDate("release_date")).toLocalDate())
                    .duration(resultSet.getInt("duration"))
                    .mpa(getMpaById(resultSet.getInt("rating_mpa_id")))
                    .genres(getGenre(resultSet.getInt("film_id")))
                    .likes(getLikes(resultSet.getInt("film_id")))
                    .build(), count);
        } else if (genre == -1 && year > 0) {
            String sqlQueryMiddle = "WHERE EXTRACT(YEAR FROM CAST(FILMS.RELEASE_DATE AS DATE)) = ? ";
            sqlQuery = sqlQueryStart + sqlQueryMiddle + sqlQueryFinish;

            return jdbcTemplate.query(sqlQuery, this::makeFilm, year, count);
        } else if (genre > 0 && year == -1) {
            String sqlQueryMiddle = "WHERE G.GENRE_ID = ? ";
            sqlQuery = sqlQueryStart + sqlQueryMiddle + sqlQueryFinish;

            return jdbcTemplate.query(sqlQuery, this::makeFilm, genre, count);
        } else {
            String sqlQueryMiddle = "WHERE G.GENRE_ID = ? AND EXTRACT(YEAR FROM CAST(FILMS.RELEASE_DATE AS DATE)) = ? ";
            sqlQuery = sqlQueryStart + sqlQueryMiddle + sqlQueryFinish;

            return jdbcTemplate.query(sqlQuery, this::makeFilm, genre, year, count);
        }
    }

    private Mpa getMpaById(int mpaId) {
        String sqlQuery =
                "SELECT rating_mpa_id, mpa_name " +
                        "FROM mpa_type " +
                        "WHERE rating_mpa_id=?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Рейтинг mpa не найден.");
        }
    }

    private void addMpa(Film film) {
            findAllMpa().forEach(mpa -> {
                if (Objects.equals(film.getMpa().getId(), mpa.getId())) {
                    film.setMpa(mpa);
                }
            });
    }

    private List<Mpa> findAllMpa() {
        List<Mpa> mpaList = new ArrayList<>();

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT rating_mpa_id, mpa_name " +
                        "FROM mpa_type");

        while (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(mpaRows.getInt("rating_mpa_id"))
                    .name(mpaRows.getString("mpa_name"))
                    .build();
            mpaList.add(mpa);
        }
        return mpaList;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("rating_mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

    private Genre getGenreForId(int id) {
        String sqlQuery =
                "SELECT genre_id, name " +
                        "FROM genre_type " +
                        "WHERE genre_id=?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Жанр не найден.");
        }
    }

    private Set<Genre> getGenre(int id) {
        Set<Genre> genreSet = new HashSet<>();

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT DISTINCT genre_id " +
                        "FROM genre " +
                        "WHERE film_id = ? " +
                        "ORDER BY genre_id ASC", id);

        while (genreRows.next()) {
            genreSet.add(getGenreForId(genreRows.getInt("genre_id")));
        }
        Set<Genre> sortedGenres = genreSet.stream()
                .sorted(Comparator.comparing(Genre::getId).thenComparing(Genre::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return sortedGenres;
    }

    private void addGenresForCurrentFilm(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }
        film.getGenres().forEach(g -> {
            String sqlQuery =
                    "INSERT INTO genre(film_id, genre_id) " +
                            "VALUES (?, ?)";

            jdbcTemplate.update(sqlQuery, film.getId(), g.getId());
        });
    }

    private void addGenreName(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }
        film.getGenres().forEach(g -> g.setName(getGenreForId(g.getId()).getName()));
    }

    private void updateGenres(Film film) {
        String sqlQuery =
                "DELETE " +
                        "FROM genre " +
                        "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresForCurrentFilm(film);
    }

    private Set<Integer> getLikes(int id) {
        Set<Integer> likes = new HashSet<>();
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet(
                "SELECT like_id, film_id, user_id " +
                        "FROM likes");

        while (likeRows.next()) {
            if (likeRows.getInt("film_id") == id) {
                likes.add(likeRows.getInt("like_id"));
            }
        }
        return likes;
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

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Mpa mpa = getMpaById(rs.getInt("rating_mpa_id"));
        Set<Genre> genres = getGenre(id);
        Set<Integer> likes = getLikes(id);

        log.info("DAO: Метод создания объекта фильма из бд с id {}", id);

        return Film.filmBl(id, name, description, duration, releaseDate, mpa, genres, likes);
    }

}
