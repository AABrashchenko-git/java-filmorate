package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("GET /users is accessed");
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("POST /users is accessed");
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("GET /users/{} is accessed", id);
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable int id) {
        log.info("DELETE /users/{} is accessed", id);
        userService.removeUser(id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updUser) {
        log.info("PUT /users is accessed");
        return userService.updateUser(updUser);
    }

}
