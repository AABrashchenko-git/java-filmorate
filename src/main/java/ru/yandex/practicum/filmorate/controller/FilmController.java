package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("GET /films is processed");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films is processed");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updFilm) {
        log.info("PUT /films is accessed");
        return filmService.updateFilm(updFilm);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("GET /films/{} is accessed", id);
        return filmService.getFilm(id);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable int id) {
        log.info("DELETE /films/{} is accessed", id);
        filmService.removeFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopRatedFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("GET /films/popular is accessed");
        return filmService.getTopRatedFilms(count);
    }

    @GetMapping("/{id}/likes")
    public Collection<Integer> getFilmLikes(@PathVariable int id) {
        // Т.к. мы убираем из сущности фильма поля, связанные с лайками, я написал еще один эндпоинт для получения
        // лайков фильма. По сути, в рамках ТЗ он не нужен, но я его использовал в тестовых классах
        // для проверки того, что логика по добавлению лайков от пользователя работает корректно
        log.info("GET /films/{}/likes is accessed", id);
        return filmService.getFilmLikes(id);
    }
}
