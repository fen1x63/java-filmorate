package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DataObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.DataObject;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryDataObjectStorage<T extends DataObject> implements DataObjectStorage<T> {

    protected final Map<Long, T> objects = new HashMap<>();
    protected Long idCounter = 0L;

    @Override
    public Collection<T> getAll() {
        return objects.values();
    }

    @Override
    public T add(@Valid T object) {
        object.setId(null);
        object.setId(manageAssignId(object));
        objects.put(object.getId(), object);
        return objects.get(object.getId());
    }

    @Override
    public T update(@Valid T object) {
        objects.put(manageAssignId(object), object);
        return objects.get(object.getId());
    }

    private Long manageAssignId(T object) {
        if (object.getId() == null) {
            if (idCounter != 0L && objects.containsKey(idCounter)) {
                while (objects.containsKey(++idCounter)) {
                }
                return idCounter;
            } else {
                return ++idCounter;
            }
        } else {
            if (object.getId() > 0) {
                return object.getId();
            } else {
                throw new DataObjectNotFoundException(object);
            }
        }
    }
}
