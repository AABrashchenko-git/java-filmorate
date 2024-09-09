package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updFilm) {
        return filmService.updateFilm(updFilm);
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @DeleteMapping("/{id}")
    public Film remove(@PathVariable int id) {
        return filmService.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFromUser(@PathVariable int id, @PathVariable int userId) {
        filmService.addLikeFromUser(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromUser(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLikeFromUser(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopRatedFilms(@RequestParam(required = false) Optional<Integer> count) {
        Integer actualCount = count.orElse(0);
        return filmService.getTopRatedFilms(actualCount);
    }

}
