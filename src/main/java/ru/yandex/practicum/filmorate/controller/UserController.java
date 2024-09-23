package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("GET /users is accessed");
        return userService.getAllUsers();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("POST /users is accessed");
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        log.info("GET /users/{} is accessed", id);
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public User remove(@PathVariable int id) {
        log.info("DELETE /users/{} is accessed", id);
        return userService.removeUser(id);
    }

    @PutMapping
    public User update(@Valid @RequestBody User updUser) {
        log.info("PUT /users is accessed");
        return userService.updateUser(updUser);
    }

}
