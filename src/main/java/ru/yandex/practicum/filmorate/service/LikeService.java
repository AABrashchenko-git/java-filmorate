package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
public class LikeService {
    private final LikeStorage likeStorage;

    public LikeService(@Qualifier("dbLikeStorage") LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    public void addLikeFromUser(Integer filmId, Integer userId) {
        likeStorage.addLikeFromUser(filmId, userId);
    }

    public void removeLikeFromUser(Integer filmId, Integer userId) {
        likeStorage.removeLikeFromUser(filmId, userId);
    }

}
