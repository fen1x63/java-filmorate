package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.annotation.AfterCinemaBirthday;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Slf4j
public class Film implements DataObject {

    Long id;

    @NotNull(message = "Name can not be null")
    @NotBlank(message = "Name can not be empty")
    String name;

    @Size(message = "Description is too long", max = 200)
    @NotBlank(message = "Description can not be empty")
    @NotNull(message = "Description can not be null")
    String description;

    @NotNull(message = "Release Date can not be null")
    @AfterCinemaBirthday(message = "The film appeared before cinematography")
    LocalDate releaseDate;

    @NotNull(message = "Duration can not be null")
    @Positive(message = "Duration is negative or zero")
    Integer duration;

}
