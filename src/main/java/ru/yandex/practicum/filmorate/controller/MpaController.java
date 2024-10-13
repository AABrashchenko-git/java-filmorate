package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAll() {
        log.info("GET /mpa is accessed");
        return mpaService.getAllMpaRatings();
    }

    @GetMapping({"/{mpaId}"})
    public Mpa getMpaById(@PathVariable Integer mpaId) {
        log.info("GET /mpa/{} is accessed", mpaId);
        return mpaService.getMpaRatingById(mpaId);
    }
}
