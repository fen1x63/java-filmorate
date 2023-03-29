package ru.yandex.practicum.filmorate.validation.validator;

import ru.yandex.practicum.filmorate.validation.annotation.NotSpace;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotSpaceValidator implements ConstraintValidator<NotSpace, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null && !s.isEmpty() && !s.isBlank()) {
            return !s.contains(" ");
        }
        return true;
    }
}
