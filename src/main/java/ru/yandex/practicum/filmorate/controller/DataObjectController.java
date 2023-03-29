package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.DataObject;

import java.util.Collection;

public interface DataObjectController<T extends DataObject> {

    ResponseEntity<Collection<T>> read();

    ResponseEntity<T> create(T object);

    ResponseEntity<T> update(T object);

}
