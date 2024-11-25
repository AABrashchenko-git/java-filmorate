package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Slf4j
@Repository("dbLikeStorage")
public class DbLikeStorage implements LikeStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addLikeFromUser(Integer filmId, Integer userId) {
        String query = """
                INSERT INTO liked_films (film_id, user_id)
                VALUES (?, ?);
                """;
        log.info("user {} liked film {}", userId, filmId);
        jdbc.update(query, filmId, userId);
    }

    @Override
    public void removeLikeFromUser(Integer filmId, Integer userId) {
        String query = """
                DELETE FROM liked_films
                WHERE film_id = ? AND user_id = ?;
                """;
        log.info("user {} removed like from film {}", userId, filmId);
        jdbc.update(query, filmId, userId);
    }
}
