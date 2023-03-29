package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DataObjectService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractDataObjectController<User> {

    @Override
    protected DataObjectService<User> getService() {
        return new UserService();
    }

    @Override
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return super.create(user);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<User>> read() {
        return super.read();
    }

    @Override
    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return super.update(user);
    }

}
