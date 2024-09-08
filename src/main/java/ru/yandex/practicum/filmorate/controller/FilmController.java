package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        log.info("GET /films request is processed");
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film {} is created", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updFilm) {
        if (films.containsKey(updFilm.getId())) {
            films.replace(updFilm.getId(), updFilm);
            log.info("Film {} is updated: {}", updFilm.getName(), updFilm);
        } else {
            throw new NotFoundException("Film is not found",
                    new Throwable("Film ID: %d ;" + updFilm.getId()).fillInStackTrace());
        }
        return updFilm;
    }

    private Integer getNextId() {
        Integer currentMaxId = films.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0);
        return ++currentMaxId;
    }

}
