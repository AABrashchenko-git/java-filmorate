package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public User remove(@PathVariable int id) {
        return userService.removeUser(id);
    }

    @PutMapping
    public User update(@Valid @RequestBody User updUser) {
        return userService.updateUser(updUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addUserAsFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.addUserAsFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeUserFromFriendsList(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.removeUserFromFriendsList(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Integer id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendsWithOtherUser(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getMutualFriendsWithOtherUser(id, otherId);
    }

}
