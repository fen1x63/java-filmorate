package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.GenreService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final UserDbStorage userDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreService genreService;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final JdbcTemplate jdbcTemplate;
    User user;
    User user2;
    User friend;
    User mutualFriend;
    Film film;
    Film film2;


    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        film.setMpa(Mpa.builder()
                .id(1)
                .name("NC-17")
                .build());

        film2 = Film.builder()
                .name("name2")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film2.setGenres(new HashSet<>());
        film2.setLikes(new HashSet<>());
        film2.setMpa(Mpa.builder()
                .id(1)
                .name("NC-17")
                .build());

        user = User.builder()
                .email("mail@mail.mail")
                .login("login")
                .birthday(LocalDate.of(1999, 8, 17))
                .build();
        user.setFriends(new HashSet<>());

        user2 = User.builder()
                .email("gmail@gmail.gmail")
                .login("nelogin")
                .birthday(LocalDate.of(2001, 6, 19))
                .build();
        user2.setFriends(new HashSet<>());

        friend = User.builder()
                .email("gmail@gmail.gmail")
                .login("nelogin")
                .birthday(LocalDate.of(2001, 6, 19))
                .build();
        friend.setFriends(new HashSet<>());

        mutualFriend = User.builder()
                .email("mutual@mutual.mutual")
                .login("mutual")
                .birthday(LocalDate.of(2001, 1, 11))
                .build();
        mutualFriend.setFriends(new HashSet<>());
    }

    @Test
    void updateFilmTest() {
        filmDbStorage.addFilm(film);
        film.setName("updateName");
        filmDbStorage.updateFilm(film);
        assertEquals("updateName", filmDbStorage.getFilmById(film.getId()).getName());
    }

    @Test
    void likeAndDeleteLikeTest() {
        filmDbStorage.addFilm(film2);
        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
        filmDbStorage.like(2, 1);
        filmDbStorage.like(2, 2);
        System.out.println(film2.getLikes().size());
        film2.setLikes(likeDbStorage.getLikesForCurrentFilm(film2.getId()));
        System.out.println(film2.getLikes().size());
        assertEquals(2, film2.getLikes().size());

        filmDbStorage.deleteLike(2, 1);
        film2.setLikes(likeDbStorage.getLikesForCurrentFilm(film2.getId()));
        System.out.println(film2.getLikes().size());
        assertEquals(1, film2.getLikes().size());
    }

    @Test
    void getRatingTest() {
        filmDbStorage.addFilm(film);
        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
        filmDbStorage.like(1, 1);
        filmDbStorage.like(1, 2);
        assertEquals(1, filmService.getTopFilms(1).get(0).getId());
    }


    @Test
    void contextLoads() {
    }

    @Test
    void addFilmTest() {
        filmDbStorage.addFilm(film);
        assertEquals(film.getId(), filmDbStorage.getFilmById(film.getId()).getId());
        assertEquals(film.getName(), filmDbStorage.getFilmById(film.getId()).getName());
        assertEquals(film.getMpa().getName(), filmDbStorage.getFilmById(film.getId()).getMpa().getName());
        assertEquals(film.getReleaseDate(), filmDbStorage.getFilmById(film.getId()).getReleaseDate());
        assertEquals(film.getDescription(), filmDbStorage.getFilmById(film.getId()).getDescription());
        assertEquals(film.getGenres(), filmDbStorage.getFilmById(film.getId()).getGenres());
        assertEquals(film.getLikes(), filmDbStorage.getFilmById(film.getId()).getLikes());
        assertEquals(film.getDuration(), filmDbStorage.getFilmById(film.getId()).getDuration());
    }

    @Test
    void findAllGenreTest() {
        List<Genre> genreListTest = genreService.findAll();
        assertEquals(6, genreListTest.size());
    }

    @Test
    void setFilmGenreTest() {
        assertTrue(film.getGenres().isEmpty());
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        assertEquals(1, film.getGenres().size());
    }

    @Test
    void getGenreForIdTest() {
        Genre genreTest = genreService.getGenre(1);
        assertEquals("Комедия", genreTest.getName());
    }

    @Test
    void addGenreTest() {
        assertTrue(film.getGenres().isEmpty());
        filmDbStorage.addFilm(film);
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        genreService.addGenreNameToFilm(film);
        assertEquals(1, film.getGenres().size());
    }

    @Test
    void updateGenreTest() {
        assertTrue(film.getGenres().isEmpty());
        filmDbStorage.addFilm(film);
        film.getGenres().add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        genreDbStorage.updateGenresForCurrentFilm(film);
        assertEquals(1, film.getGenres().size());
    }

    @Test
    void findAllMpaTest() {
        List<Mpa> mpaListTest = mpaDbStorage.findAll();
        assertEquals(5, mpaListTest.size());
    }

    @Test
    void getMpaForIdTest() {
        Mpa mpaTest = mpaDbStorage.getMpa(5);
        assertEquals("NC-17", mpaTest.getName());
    }

    @Test
    void addMpaInFilmTest() {
        film.setMpa(Mpa.builder()
                .id(1)
                .name("NC-17")
                .build());
        mpaDbStorage.addMpaToFilm(film);
        assertNotNull(film.getMpa());
    }

    @Test
    void shouldCreateAndUpdateAndGetUser() {
        userDbStorage.addUser(user);
        assertEquals(user, userDbStorage.getUserById(user.getId()));
        assertEquals(user.getLogin(), userDbStorage.getUserById(user.getId()).getName());

        user.setEmail("lol@lol.lol");
        userDbStorage.updateUser(user);
        assertEquals(user, userDbStorage.getUserById(user.getId()));

        assertEquals(1, userDbStorage.findAllUsers().size());
        assertEquals(user, userDbStorage.getUserById(user.getId()));
    }


    @Test
    void shouldAddAndDeleteFriends() {
        userDbStorage.addUser(user);
        userDbStorage.addUser(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());
        assertEquals(1, userDbStorage.getFriendsByUserId(user.getId()).size());
        assertEquals(0, userDbStorage.getFriendsByUserId(friend.getId()).size());

        userDbStorage.deleteFriend(user.getId(), friend.getId());
        assertEquals(0, userDbStorage.getFriendsByUserId(user.getId()).size());
        assertEquals(0, userDbStorage.getFriendsByUserId(friend.getId()).size());
    }


    @Test
    void shouldGetMutualFriends() {
        userDbStorage.addUser(user);
        userDbStorage.addUser(friend);
        userDbStorage.addUser(mutualFriend);
        userDbStorage.addFriend(user.getId(), mutualFriend.getId());
        userDbStorage.addFriend(friend.getId(), mutualFriend.getId());
        assertSame(userDbStorage.getMutualFriends(user.getId(), friend.getId()).get(0).getId(), mutualFriend.getId());
    }
}
