package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;

@Service
@Validated
public class UserService extends AbstractDataObjectService<User> {

}
