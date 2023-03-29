package ru.yandex.practicum.filmorate.validation.annotation;

import ru.yandex.practicum.filmorate.validation.validator.AfterCinemaBirthdayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AfterCinemaBirthdayValidator.class)
@Documented
public @interface AfterCinemaBirthday {

    String message() default "{AfterCinemaBirthdayValidator.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
