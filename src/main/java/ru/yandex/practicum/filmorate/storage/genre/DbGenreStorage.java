package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbc;

    @Override
    public List<Genre> getAllGenres() {
        String query = """
                SELECT *
                FROM genre;
                """;
        return jdbc.query(query, (rs, rowNum) ->
                Genre.builder().id(rs.getInt("genre_id")).name(rs.getString("name")).build());
    }

    @Override
    public Genre getGenreById(Integer id) {
        if (!genreExists(id)) {
            throw new NotFoundException(String.format("Genre with id = %d is not found", id));
        }
        String query = """
                SELECT *
                FROM genre
                WHERE genre_id = ?;
                """;
        return jdbc.queryForObject(query, new Object[]{id}, (rs, rowNum) ->
                Genre.builder().id(rs.getInt("genre_id")).name(rs.getString("name")).build());
    }

    private boolean genreExists(int genreId) {
        String query = """
                SELECT COUNT(*)
                FROM genre
                WHERE genre_id = ?;
                """;
        Integer count = jdbc.queryForObject(query, Integer.class, genreId);
        return count != null && count > 0;
    }
}
