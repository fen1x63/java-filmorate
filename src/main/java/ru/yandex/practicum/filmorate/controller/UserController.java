package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" "))
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Логин не должен содержать пробелы");
        int id = generateId();
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        users.put(id, user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User saveUser(@Valid @RequestBody User user) {
        int id = user.getId();
        if (id < 0) throw new ValidationException("PUT: отрицательный id " + id);
        if (!users.containsKey(id)) throw new ValidationException("PUT: несуществующий id" + id);
        if (id == 0) {
            id = generateId();
            user.setId(id);
            users.put(id, user);
            log.info("Добавлен новый пользователь {}", user);
        } else {
            users.put(user.getId(), user);
            log.info("Данные пользователя {} успешно обновлены", user);
        }
        return user;
    }

    @ExceptionHandler(ru.yandex.practicum.filmorate.exception.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException
            (ru.yandex.practicum.filmorate.exception.ValidationException e) {
        log.warn("Ошибка валидации: " + e.getMessage());
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
}