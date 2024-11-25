package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilm(Integer filmId);

    Collection<Film> getTopRatedFilms(Integer count);

    Film addFilm(Film film);

    Film updateFilm(Film updFilm);

    void removeFilm(Integer id);

    Collection<Integer> getFilmLikes(Integer id);

}
