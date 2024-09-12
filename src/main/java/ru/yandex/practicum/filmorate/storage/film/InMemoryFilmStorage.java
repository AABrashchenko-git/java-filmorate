package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        log.info("/films get all films handled");
        return films.values();
    }

    @Override
    public Film get(Integer filmId) {
        if (!films.containsKey(filmId)) {
            //логирование отсюда и в других подобных местах(not found) убрал, т.к. выбрасывание
            // исключения логируется в обработчике исключений с уровнем логирования WARN
            throw new NotFoundException(String.format("Film with id = %d is not found", filmId));
        }
        log.info("/films/{} handled", filmId);
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getTopRated(Integer count) {
        Stream<Film> topRatedFilmsStream = films.values()
                .stream().sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed());
        if (count == 0) {
            log.info("10 most popular films handled");
            return topRatedFilmsStream.limit(10).toList();
        } else {
            log.info("{} most popular films handled", count);
            return topRatedFilmsStream.limit(count).toList();
        }
    }

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film {} is created", film.getName());
        return film;
    }

    @Override
    public Film update(Film updFilm) {
        if (films.containsKey(updFilm.getId())) {
            films.replace(updFilm.getId(), updFilm);
            log.info("Film {} is updated: {}", updFilm.getName(), updFilm);
        } else {
            throw new NotFoundException("Film is not found",
                    new Throwable("Film ID: %d ;" + updFilm.getId()).fillInStackTrace());
        }
        return updFilm;
    }

    @Override
    public Film remove(Integer filmId) {
        Film filmToRemove = films.get(filmId);
        if (filmToRemove != null) {
            films.remove(filmId);
            log.info("Film with id = {} is removed", filmId);
        } else {
            throw new NotFoundException("Film is not found",
                    new Throwable("Film ID: %d ;" + filmId).fillInStackTrace());
        }
        return filmToRemove;
    }

    private Integer getNextId() {
        Integer currentMaxId = films.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0);
        return ++currentMaxId;
    }
}
