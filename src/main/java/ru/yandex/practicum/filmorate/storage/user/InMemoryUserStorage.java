package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int idForUser = 0;

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        userValidation(user);
        user.setFriends(new HashSet<>());
        user.setId(getIdForUser());
        users.put(user.getId(), user);
        log.info("Поступил запрос на добавление пользователя. Пользователь добавлен.");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.get(user.getId()) != null) {
            userValidation(user);
            user.setFriends(new HashSet<>());
            users.put(user.getId(), user);
            log.info("Поступил запрос на изменения пользователя. Пользователь изменён.");
        } else {
            log.error("Поступил запрос на изменения пользователя. Пользователь не найден.");
            throw new EntityNotFoundException("User not found.");
        }
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else throw new EntityNotFoundException("User not found.");
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return findAllUsers().stream()
                .filter(user -> user.getFriends().contains(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer friendId) {
        List<User> mutualFriends = new ArrayList<>();
        Set<Integer> userFriends = getUserById(userId).getFriends();
        Set<Integer> friendFriends = getUserById(friendId).getFriends();
        for (Integer id : userFriends) {
            if (friendFriends.contains(id)) {
                mutualFriends.add(getUserById(id));
            }
        }
        return mutualFriends;
    }


    private int getIdForUser() {
        return ++idForUser;
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        Set<Integer> userFriends = getUserById(userId).getFriends();
        Set<Integer> friendFriends = getUserById(friendId).getFriends();
        userFriends.add(friendId);
        friendFriends.add(userId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        Set<Integer> userFriends = getUserById(userId).getFriends();
        Set<Integer> friendFriends = getUserById(friendId).getFriends();
        userFriends.remove(friendId);
        friendFriends.remove(userId);
        return getUserById(userId);
    }

}