package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(int id) {
        return filmStorage.get(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film updFilm) {
        return filmStorage.update(updFilm);
    }

    public Film removeFilm(int id) {
        return filmStorage.remove(id);
    }

    public void addLikeFromUser(Integer filmId, Integer userId) {
        filmStorage.get(filmId).getLikes().add(userId); // добавляем лайк фильму
        userStorage.get(userId).getLikedFilms().add(filmId); // сохраняем лайк в список liked фильмов юзера
    }

    public void removeLikeFromUser(Integer filmId, Integer userId) {
        filmStorage.get(filmId).getLikes().remove(userId);
        userStorage.get(userId).getLikedFilms().remove(filmId);
    }

    public Collection<Film> getTopRatedFilms(Integer count) {
        Stream<Film> topRatedFilmsStream = filmStorage.getAll()
                .stream().sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed());
        if (count == 0) {
            log.info("GET /films/popular is processed");
            return topRatedFilmsStream.limit(10).toList();
        } else {
            log.info("GET /films/popular?count={} is processed", count);
            return topRatedFilmsStream.limit(count).toList();
        }
    }
}
