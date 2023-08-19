package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }
}
