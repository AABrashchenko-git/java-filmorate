package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;

    public void addLikeFromUser(Integer filmId, Integer userId) {
        likeStorage.addLikeFromUser(filmId, userId);
    }

    public void removeLikeFromUser(Integer filmId, Integer userId) {
        likeStorage.removeLikeFromUser(filmId, userId);
    }

}
