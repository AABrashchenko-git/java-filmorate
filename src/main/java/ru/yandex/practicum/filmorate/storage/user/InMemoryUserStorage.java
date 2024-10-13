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
        log.info("/users get all users handled");
        return users.values();
    }

    @Override
    public User get(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("user with id = %d is not found", userId));
        }
        log.info("get /users/{} handled", userId);
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
    public void remove(Integer userId) {
        if (users.get(userId) != null) {
            users.remove(userId);
            log.info("User with id = {} is removed", userId);
        } else {
            throw new NotFoundException("User is not found",
                    new Throwable("User ID: %d ;" + userId).fillInStackTrace());
        }
        users.get(userId);
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
