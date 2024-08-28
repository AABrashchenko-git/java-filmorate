package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        user.setId(getNextId());
        user.setName(getDisplayedName(user));
        users.put(user.getId(), user);
        log.info("User {} is created", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updUser) {

        if (users.containsKey(updUser.getId())) {
            users.replace(updUser.getId(), updUser);
            log.info("User {} is updated: {}", updUser.getLogin(), updUser);
        } else {
            throw new NotFoundException("User is not found");
        }
        return updUser;
    }

    private Integer getNextId() {
        Integer currentMaxId = users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0);
        return ++currentMaxId;
    }

    private String getDisplayedName(User user) {
        return user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getLogin();
    }

}
