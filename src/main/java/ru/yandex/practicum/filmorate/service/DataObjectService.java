package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.DataObject;

import java.util.Collection;

public interface DataObjectService<T extends DataObject> {

    Collection<T> getAll();

    T add(T object);

    T update(T object);

}
