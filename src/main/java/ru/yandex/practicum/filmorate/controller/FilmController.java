package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@RestController
//Logbook пока отключил, оставил везде Slf4j, чтобы не перегружать лог всеми деталями http-запросов
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("GET /films is processed");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("POST /films is processed");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updFilm) {
        log.info("PUT /films is accessed");
        return filmService.updateFilm(updFilm);
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable int id) {
        log.info("GET /films/{} is accessed", id);
        return filmService.getFilm(id);
    }

    @DeleteMapping("/{id}")
    public Film remove(@PathVariable int id) {
        log.info("DELETE /films/{} is accessed", id);
        return filmService.removeFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopRatedFilms(@RequestParam(required = false) Optional<Integer> count) {
        Integer actualCount = count.orElse(0);
        log.info("GET /films/popular is accessed");
        return filmService.getTopRatedFilms(actualCount);
    }

}
