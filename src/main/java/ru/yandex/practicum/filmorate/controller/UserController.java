package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RestController("")
@RequestMapping("/users")
public class UserController {

    private static Integer currentMaxId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {

        user.setId(currentMaxId);
        users.put(currentMaxId, user);
        log.info("createUser: {}", user);
        return user;
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@Valid @RequestBody User user) {

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("updateUser: {}", user);
            return ResponseEntity.status(HttpStatus.OK).body("Пользователь изменён");
        } else {

            log.info("error updateUser without ID: {}", user);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Пользователь не найден");
    }

    @GetMapping
    public List<User> findAll() {
        log.info("findAll");
        return users.values().stream().collect(Collectors.toList());
    }


}
