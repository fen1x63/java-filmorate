package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class User {

    @NotNull
    private int id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$")
    private String login;

    private String name;

    @PastOrPresent
    @NotNull
    private LocalDate birthday;
}
