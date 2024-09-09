package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAll();

    Film get(Integer filmId);

    Film add(Film film);

    Film update(Film updFilm);

    Film remove(Integer id);

}
