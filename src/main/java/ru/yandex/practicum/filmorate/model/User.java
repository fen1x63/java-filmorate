package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.annotation.NotSpace;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Slf4j
public class User implements DataObject {

    Long id;

    @NotNull(message = "E-mail can not be null")
    @NotBlank(message = "E-mail can not be empty")
    //regexp RFC 5322
    @Email(message = "Wrong e-mail format", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\" +
            ".[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
            "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)" +
            "+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\" +
            ".){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]" +
            ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    String email;

    @NotNull(message = "Login can not be null")
    @NotBlank(message = "Login can not be empty")
    @NotSpace(message = "Login can not contains spaces")
    String login;

    String name;

    @NotNull(message = "Birthday can not be null")
    @Past(message = "Birthday can not be in the future")
    LocalDate birthday;

    @JsonCreator
    public User(@JsonProperty("id") Long id,
                @JsonProperty("email") String email,
                @JsonProperty("login") String login,
                @JsonProperty("name") String name,
                @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = getNameOrLogin(name, login);
        this.birthday = birthday;
    }

    private String getNameOrLogin(String name, String login) {
        if (name == null || name.isBlank()) {
            log.info("Field name is assigned field login");
            return login;
        } else {
            return name;
        }

    }

}
