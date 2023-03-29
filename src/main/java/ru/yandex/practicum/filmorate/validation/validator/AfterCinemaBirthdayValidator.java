package ru.yandex.practicum.filmorate.validation.validator;

import ru.yandex.practicum.filmorate.validation.annotation.AfterCinemaBirthday;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterCinemaBirthdayValidator implements ConstraintValidator<AfterCinemaBirthday, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date != null) {
            return date.isAfter(LocalDate.of(1895, 12, 28));
        }
        return true;
    }

}
