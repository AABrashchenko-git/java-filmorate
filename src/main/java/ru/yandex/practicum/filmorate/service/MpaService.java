package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings();
    }

    public Mpa getMpaRatingById(Integer id) {
        return mpaStorage.getMpaRatingById(id);
    }
}
