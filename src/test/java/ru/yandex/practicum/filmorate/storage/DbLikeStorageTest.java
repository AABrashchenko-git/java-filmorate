package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.DbLikeStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({DbLikeStorage.class, DbFilmStorage.class, DbUserStorage.class, FilmMapper.class, UserMapper.class})
public class DbLikeStorageTest {

    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private Film film1;
    private User user1;

    @Autowired
    public DbLikeStorageTest(@Qualifier("dbLikeStorage") LikeStorage likeStorage, @Qualifier("dbFilmStorage") FilmStorage filmStorage, @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.likeStorage = likeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @BeforeEach
    void beforeEach() {
        // Создаем тестовые данные
        Set<Genre> testGenres = new HashSet<>();
        testGenres.add(new Genre(1, "Комедия"));

        film1 = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .build();

        user1 = User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        film1.getGenres().addAll(testGenres);

        filmStorage.add(film1);
        userStorage.add(user1);
    }

    @Test
    void testAddLikeFromUser() {
        likeStorage.addLikeFromUser(film1.getId(), user1.getId());

        // Проверяем, что лайк добавлен
        Film updatedFilm = filmStorage.get(film1.getId());
        assertThat(updatedFilm.getLikes()).contains(user1.getId());
    }

    @Test
    void testRemoveLikeFromUser() {
        likeStorage.addLikeFromUser(film1.getId(), user1.getId());

        // Проверяем, что лайк добавлен
        Film updatedFilm = filmStorage.get(film1.getId());
        assertThat(updatedFilm.getLikes()).contains(user1.getId());

        // Удаляем лайк
        likeStorage.removeLikeFromUser(film1.getId(), user1.getId());

        // Проверяем, что лайк удален
        Film filmAfterRemove = filmStorage.get(film1.getId());
        assertThat(filmAfterRemove.getLikes()).doesNotContain(user1.getId());
    }
}