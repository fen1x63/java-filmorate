package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.DataObject;
import ru.yandex.practicum.filmorate.storage.DataObjectStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryDataObjectStorage;

import java.util.Collection;

@Service
public abstract class AbstractDataObjectService<T extends DataObject> implements DataObjectService<T> {

    protected final DataObjectStorage<T> objectStorage = new InMemoryDataObjectStorage<>();

    @Override
    public Collection<T> getAll() {
        return objectStorage.getAll();
    }

    @Override
    public T add(T object) {
        return objectStorage.add(object);
    }

    @Override
    public T update(T object) {
        return objectStorage.update(object);
    }
}
