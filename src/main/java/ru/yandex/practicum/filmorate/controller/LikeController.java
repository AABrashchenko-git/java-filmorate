package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.LikeService;


@RestController
@Slf4j
@RequestMapping("/films/{id}/like/{userId}")
public class LikeController {
    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PutMapping
    public void addLikeFromUser(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /films/{}/like/{} is accessed", id, userId);
        likeService.addLikeFromUser(id, userId);
    }

    @DeleteMapping
    public void removeLikeFromUser(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("DELETE /films/{}/like/{} is accessed", id, userId);
        likeService.removeLikeFromUser(id, userId);
    }

}
