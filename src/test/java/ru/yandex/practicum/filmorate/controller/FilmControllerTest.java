package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.TestUtil;
import ru.yandex.practicum.filmorate.model.Film;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerTest extends AbstractControllerTest {

    @Autowired
    protected FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController.getFilms().put(TestUtil.validFilm1.getId(), TestUtil.validFilm1);
        filmController.getFilms().put(TestUtil.validFilm2.getId(), TestUtil.validFilm2);
    }

    @AfterEach
    void cleanData() {
        filmController.getFilms().clear();
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films/film")
                                .content("{\"id\":1,\"description\":\"description\",\"releaseDate\":\"1895-12-29\"," +
                                        "\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films/film")
                                .content("{\"id\":1,\"name\":\" \",\"description\":\"description\",\"releaseDate\":" +
                                        "\"1895-12-29\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films/film")
                                .content("{\"id\":1,\"name\":\"New film\",\"description\":\"description\"," +
                                        "\"releaseDate\":\"1895-12-27\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films/film")
                                .content("{\"id\":1,\"name\":\"New film\",\"description\":\"012345678901234567890" +
                                        "123456789012345678901234567890123456789012345678901234567890123456789012" +
                                        "345678901234567890123456789012345678901234567890123456789012345678901234" +
                                        "567890123456789012345678901234567891\"," +
                                        "\"releaseDate\":\"1895-12-29\"," +
                                        "\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddFilmCorrectly() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films/film")
                                .content(String.valueOf(TestUtil.objectToJson(filmController.getFilms().get(1))))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"name\":\"validFilm1\"," +
                                "\"description\":\"validFilm1 description\"," +
                                "\"releaseDate\":\"2020-10-10\"," +
                                "\"filmDuration\":\"PT2H40M\"}"));
    }

    @Test
    void shouldUpdateFilmCorrectly() throws Exception {
        Film oldFilm = filmController.getFilms().get(1);
        Film newFilm = Film.builder().filmDuration(oldFilm.getFilmDuration()).description(oldFilm.getDescription())
                .name("New Name").releaseDate(oldFilm.getReleaseDate())
                .build();
        newFilm.setId(oldFilm.getId());
        String filmAsString = TestUtil.objectToJson(newFilm);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films/film")
                                .content(filmAsString)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"name\":\"New Name\",\"description\":\"validFilm1 description\"," +
                                "\"releaseDate\":\"2020-10-10\",\"filmDuration\":\"PT2H40M\"}"));
    }

    @Test
    void shouldReturnAllFilms() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[" +
                                "{\"id\":1," +
                                "\"name\":\"validFilm1\"," +
                                "\"description\":\"validFilm1 description\"," +
                                "\"releaseDate\":\"2020-10-10\"," +
                                "\"filmDuration\":\"PT2H40M\"}," +
                                "{\"id\":2,\"name\":\"validFilm2\"," +
                                "\"description\":\"validFilm2 description\"," +
                                "\"releaseDate\":\"2021-10-10\"," +
                                "\"filmDuration\":\"PT2H40M\"}" +
                                "]"));
    }
}
