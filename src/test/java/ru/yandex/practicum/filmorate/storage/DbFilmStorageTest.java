package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.DbGenreStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({DbFilmStorage.class, DbGenreStorage.class, FilmMapper.class})
public class DbFilmStorageTest {

    private final FilmStorage filmStorage;
    private Film film1;
    private Film film2;

    @Autowired
    public DbFilmStorageTest(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @BeforeEach
    void beforeEach() {
        // проходят по критериям валидации
        Set<Genre> testGenres1 = new HashSet<>();
        testGenres1.add(new Genre(1, "Комедия"));
        testGenres1.add(new Genre(2, "Драма"));

        Set<Genre> testGenres2 = new HashSet<>();
        testGenres2.add(new Genre(2, "Драма"));

        film1 = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .build();
        film2 = Film.builder()
                .name("film2Name")
                .description("film2Description")
                .duration(120)
                .releaseDate(LocalDate.now().minusYears(20))
                .mpa(new Mpa(2, "PG"))
                .build();
        film1.getGenres().addAll(testGenres1);
        film2.getGenres().addAll(testGenres2);

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
    }

    @Test
    void testGetFilmById() {
        Film receivedFilm1 = filmStorage.getFilm(1);
        Film receivedFilm2 = filmStorage.getFilm(2);
        assertThat(receivedFilm1).isNotNull();
        assertThat(receivedFilm1.getId()).isEqualTo(1);
        Assertions.assertEquals(film2, receivedFilm2);
    }

    @Test
    void testGetAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        System.out.println(films);
        assertEquals(films.size(), 2);
        assertThat(films).hasSizeGreaterThan(0);
    }

    @Test
    void testUpdateFilm() {
        Film existingFilm = filmStorage.getFilm(1);
        existingFilm.setName("Updated Film");
        existingFilm.setDescription("Updated Description");

        Film updatedFilm = filmStorage.updateFilm(existingFilm);
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testRemoveFilm() {
        filmStorage.removeFilm(1);
        assertThrows(NotFoundException.class, () -> filmStorage.getFilm(1));
    }

}

