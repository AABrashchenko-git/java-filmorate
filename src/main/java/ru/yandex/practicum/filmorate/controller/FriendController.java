package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{id}/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PutMapping("/{friendId}")
    public void addUserAsFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("PUT /users/{}/friends/{} is accessed", id, friendId);
        friendService.addUserAsFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public void removeUserFromFriendsList(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("DELETE /users/{}/friends/{} is accessed", id, friendId);
        friendService.removeUserFromFriendsList(id, friendId);
    }

    @GetMapping
    public List<User> getUserFriends(@PathVariable Integer id) {
        log.info("GET /users/{}/friends is accessed", id);
        return friendService.getUserFriends(id);
    }

    @GetMapping("/common/{otherId}")
    public List<User> getMutualFriendsWithOtherUser(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("GET /users/{}/friends/common/{} is accessed", id, otherId);
        return friendService.getMutualFriendsWithOtherUser(id, otherId);
    }
}
