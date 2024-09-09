package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        log.info("GET /users request is processed");
        return users.values();
    }

    @Override
    public User get(Integer userId) {
        if (!users.containsKey(userId)) {
            log.info("User /users/{} is not found", userId);
            throw new NotFoundException(String.format("user with id = %d is not found", userId));
        }
        log.info("GET /users/{} request is processed", userId);
        return users.get(userId);
    }

    @Override
    public User add(User user) {
        user.setId(getNextId());
        user.setName(getDisplayedName(user));
        users.put(user.getId(), user);
        log.info("User {} is created", user.getLogin());
        return user;
    }

    @Override
    public User update(User updUser) {
        if (users.containsKey(updUser.getId())) {
            users.replace(updUser.getId(), updUser);
            log.info("User {} is updated: {}", updUser.getLogin(), updUser);
        } else {
            throw new NotFoundException("User is not found",
                    new Throwable("User ID: %d ;" + updUser.getId()).fillInStackTrace());
        }
        return updUser;
    }

    @Override
    public User remove(Integer userId) {
        User userToRemove = users.get(userId);
        if (userToRemove != null) {
            users.remove(userId);
            log.info("User with id = {} is removed", userId);
        } else {
            log.info("User with id = {} is not found", userId);
            throw new NotFoundException("User is not found",
                    new Throwable("User ID: %d ;" + userId).fillInStackTrace());
        }
        return userToRemove;
    }

    private Integer getNextId() {
        Integer currentMaxId = users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0);
        return ++currentMaxId;
    }

    private String getDisplayedName(User user) {
        return user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getLogin();
    }
}
