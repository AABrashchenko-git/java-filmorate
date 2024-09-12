package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    void addLikeFromUser(Integer filmId, Integer userId);

    void removeLikeFromUser(Integer filmId, Integer userId);
}
