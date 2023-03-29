package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

public class UserTest extends AbstractDataObjectTest<User> {

    String wrongJsonDir = super.jsonDir + "/wrong/model/user";
    String correctJsonDir = super.jsonDir + "/correct/model/user";

    @Override
    @Test
    public void validateWrong() {
        failedValidation(User.class, wrongJsonDir);
    }

    @Override
    @Test
    public void validateCorrect() {
        correctValidation(User.class, correctJsonDir);
    }
}
