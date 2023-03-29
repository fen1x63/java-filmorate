package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

public class FilmTest extends AbstractDataObjectTest<Film> {

    String wrongJsonDir = super.jsonDir + "/wrong/model/film";
    String correctJsonDir = super.jsonDir + "/correct/model/film";

    @Override
    @Test
    public void validateWrong() {
        failedValidation(Film.class, wrongJsonDir);
    }

    @Override
    @Test
    public void validateCorrect() {
        correctValidation(Film.class, correctJsonDir);
    }
}
