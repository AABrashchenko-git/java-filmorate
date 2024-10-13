package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class DbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbc;

    @Override
    public List<Mpa> getAllMpaRatings() {
        String query = """
                SELECT *
                FROM mpa_rating;
                """;
        log.info("/mpa get all ratings handled");
        return jdbc.query(query, (rs, rowNum) ->
                Mpa.builder().id(rs.getInt("rating_id")).name(rs.getString("name")).build());
    }

    @Override
    public Mpa getMpaRatingById(Integer id) {
        if (!mpaExists(id)) {
            throw new NotFoundException(String.format("MPA rating with id = %d is not found", id));
        }
        String query = """
                SELECT *
                FROM mpa_rating
                WHERE rating_id = ?;
                """;
        log.info("/mpa/id get rating handled");
        return jdbc.queryForObject(query, (rs, rowNum) ->
                Mpa.builder().id(rs.getInt("rating_id")).name(rs.getString("name")).build(), id);
    }

    private boolean mpaExists(int mpaId) {
        String query = """
                SELECT COUNT(*)
                FROM mpa_rating
                WHERE rating_id = ?;
                """;
        Integer count = jdbc.queryForObject(query, Integer.class, mpaId);
        return count != null && count > 0;
    }
}
