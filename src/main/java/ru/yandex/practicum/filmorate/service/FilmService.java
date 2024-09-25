package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

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

    public Collection<Film> getTopRatedFilms(Integer count) {
        return filmStorage.getTopRated(count);
    }
}
