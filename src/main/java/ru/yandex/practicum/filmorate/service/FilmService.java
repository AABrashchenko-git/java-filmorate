package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film updFilm) {
        return filmStorage.updateFilm(updFilm);
    }

    public void removeFilm(int id) {
        filmStorage.removeFilm(id);
    }

    public Collection<Film> getTopRatedFilms(Integer count) {
        return filmStorage.getTopRatedFilms(count);
    }

    public Collection<Integer> getFilmLikes(Integer id) {
        return filmStorage.getFilmLikes(id);
    }


}
