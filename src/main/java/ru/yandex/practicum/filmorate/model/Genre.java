package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import javax.validation.constraints.NotNull;

@Builder
public class Genre {

    @NotNull
    private Integer id;
    @NotNull
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
