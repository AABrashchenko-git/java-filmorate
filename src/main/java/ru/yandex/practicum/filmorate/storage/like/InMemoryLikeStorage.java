package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Component
@Slf4j
public class InMemoryLikeStorage implements LikeStorage {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryLikeStorage(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLikeFromUser(Integer filmId, Integer userId) {
        filmStorage.get(filmId).getLikes().add(userId);
        userStorage.get(userId).getLikedFilms().add(filmId);
        log.info("user {} liked film {}", userId, filmId);
    }

    @Override
    public void removeLikeFromUser(Integer filmId, Integer userId) {
        filmStorage.get(filmId).getLikes().remove(userId);
        userStorage.get(userId).getLikedFilms().remove(filmId);
        log.info("user {} removed like from film {}", userId, filmId);
    }


}
