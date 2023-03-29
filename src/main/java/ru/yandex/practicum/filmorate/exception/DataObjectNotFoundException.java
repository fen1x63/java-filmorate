package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.DataObject;

public class DataObjectNotFoundException extends RuntimeException {

    public DataObjectNotFoundException(DataObject object) {
        super("Object " + object.getClass().getSimpleName() + " with id=" + object.getId() + " not found");
    }

}
