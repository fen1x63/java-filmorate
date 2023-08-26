package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Like {

    @NotNull
    private Integer id;
    @NotNull
    @Positive
    private Integer userId;
    @NotNull
    @Positive
    private Integer filmId;
}
