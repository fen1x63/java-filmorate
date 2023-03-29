package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.DataObject;
import ru.yandex.practicum.filmorate.service.DataObjectService;

import java.util.*;

@Slf4j
public abstract class AbstractDataObjectController<T extends DataObject> implements DataObjectController<T> {


    protected final DataObjectService<T> service = getService();

    protected abstract DataObjectService<T> getService();

    @Override
    public ResponseEntity<T> create(T object) {
        log.debug(object.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(object));
    }

    @Override
    public ResponseEntity<Collection<T>> read() {
        Collection<?> collection = service.getAll();
        Iterator<?> it = collection.iterator();
        if (it.hasNext()){
            log.debug("Return {} {}-objects", collection.size(), it.next().getClass().getSimpleName());
        } else {
            log.debug("Return zero objects");
        }
        return ResponseEntity.ok(service.getAll());
    }

    @Override
    public ResponseEntity<T> update(T object) {
        log.debug(object.toString());
        if (object.getId() == null) {
            return create(object);
        }
        OptionalLong optLong = service.getAll()
                .stream()
                .mapToLong(DataObject::getId)
                .filter(object.getId()::equals)
                .findFirst();
        T resultObject = service.update(object);
        if (optLong.isPresent()) {
            return ResponseEntity.ok(resultObject);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(resultObject);
        }
    }
}
