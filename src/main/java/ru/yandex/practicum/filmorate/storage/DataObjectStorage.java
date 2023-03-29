package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.DataObject;

import java.util.Collection;

public interface DataObjectStorage<T extends DataObject> {


    Collection<T> getAll();

    T add(T object);

    T update(T object);

}
