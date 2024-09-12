package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
public class LikeService {
    private final LikeStorage likeStorage;

    @Autowired
    public LikeService(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    public void addLikeFromUser(Integer filmId, Integer userId) {
        likeStorage.addLikeFromUser(filmId, userId);
    }

    public void removeLikeFromUser(Integer filmId, Integer userId) {
        likeStorage.removeLikeFromUser(filmId, userId);
    }

}
