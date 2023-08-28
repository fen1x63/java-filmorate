package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Mpa {

    @NotNull
    private Integer id;
    @NotNull
    private String name;
}
