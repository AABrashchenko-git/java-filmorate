package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InvalidInfoException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Repository("dbFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmMapper mapper;

    @Override
    public Film get(Integer filmId) {
        filmExists(filmId);
        String getFilmQuery = """
                SELECT f.*, r.name AS mpa_name
                FROM films f
                JOIN mpa_rating r ON f.rating_id = r.rating_id
                WHERE f.film_id = ?;
                """;
        Film film = jdbc.queryForObject(getFilmQuery, mapper, filmId);
        return addGenresAndLikesToSingleFilm(film);
    }

    @Override
    public Collection<Film> getAll() {
        String query = """
                SELECT f.*, r.name AS mpa_name
                FROM films f
                JOIN mpa_rating r ON f.rating_id = r.rating_id
                ORDER BY f.film_id;
                """;
        Collection<Film> films = jdbc.query(query, mapper);
        log.info("/films get all films handled");
        return addGenresAndLikesToFilmCollection(films);
    }

    @Override
    public Collection<Film> getTopRated(Integer number) {
        String query = """
                SELECT f.*, r.name AS mpa_name
                FROM films AS f
                JOIN (
                SELECT film_id, COUNT(user_id) AS likes
                FROM liked_films
                GROUP BY film_id
                ) AS lf ON f.film_id = lf.film_id
                JOIN mpa_rating r ON f.rating_id = r.rating_id
                ORDER BY lf.likes DESC
                LIMIT ?;
                """;
        Collection<Film> topRatedFilms = jdbc.query(query, mapper, number);
        log.info("get {} most popular films handled", number);
        return addGenresAndLikesToFilmCollection(topRatedFilms);
    }

    @Override
    public Film add(Film film) {
        if (!filmValid(film)) {
            throw new InvalidInfoException("MPA or Genres are invalid");
        }
        String addFilmQuery = """
                INSERT INTO films (name, description, release_date, duration, rating_id)
                VALUES (?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(addFilmQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        String addGenresQuery = """
                INSERT INTO films_genre (film_id, genre_id)
                VALUES (?, ?);
                """;
        if (film.getGenres() != null) {
            film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            for (Genre genre : film.getGenres()) {
                jdbc.update(addGenresQuery, film.getId(), genre.getId());
            }
        }
        log.info("Film {} is created", film.getName());
        return film;
    }

    @Override
    public Film update(Film updFilm) {
        filmExists(updFilm.getId());
        String query = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration  = ?, rating_id  = ?
                WHERE film_id = ?;
                """;
        jdbc.update(
                query, updFilm.getName(), updFilm.getDescription(), Date.valueOf(updFilm.getReleaseDate()),
                updFilm.getDuration(), updFilm.getMpa().getId(), updFilm.getId());

        // 2. обновить жанры
        // Удаляем существующие жанры фильма
        String deleteGenresQuery = """
                DELETE FROM films_genre
                WHERE film_id = ?;
                """;
        jdbc.update(deleteGenresQuery, updFilm.getId());
        // Добавляем новые жанры фильма
        String insertGenresQuery = """
                INSERT INTO films_genre (film_id, genre_id)
                VALUES (?, ?);
                """;
        if (updFilm.getGenres() != null)
            updFilm.getGenres().forEach(g -> jdbc.update(insertGenresQuery, updFilm.getId(), g.getId()));

        log.info("Film {} is updated: {}", updFilm.getName(), updFilm);
        return updFilm;
    }

    @Override
    public void remove(Integer id) {
        filmExists(id);
        String query = """
                DELETE FROM films
                WHERE film_id = ?;
                """;
        int rowsAffected = jdbc.update(query, id);
        if (rowsAffected == 0) {
            throw new IllegalStateException("Ошибка при удалении фильма с id " + id);
        }
        log.info("Film with id = {} is removed", id);
    }

    private Film addGenresAndLikesToSingleFilm(Film film) {
        // Получаем жанры фильма
        String sqlGenres = """
                SELECT g.genre_id AS genre_id, g.name AS name
                FROM films_genre fg
                JOIN genre g ON fg.genre_id = g.genre_id
                WHERE fg.film_id = ?;
                """;
        List<Genre> genres = jdbc.query(sqlGenres,
                (rs, rowNum) -> Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("name")).build(),
                film.getId());

        // Получаем лайки фильма
        String sqlLikes = """
                SELECT user_id
                FROM liked_films
                WHERE film_id = ?;
                """;
        Set<Integer> likes = new HashSet<>(jdbc.queryForList(sqlLikes, new Object[]{film.getId()}, Integer.class));

        // Заполняем объект Film
        film.getGenres().addAll(genres);
        film.getLikes().addAll(likes);
        log.info("/films/{} handled", film.getId());
        return film;
    }

    private Collection<Film> addGenresAndLikesToFilmCollection(Collection<Film> films) {
        // Получаем все лайки
        String getAllLikesQuery = """
                SELECT film_id, user_id
                FROM liked_films;
                """;
        Map<Integer, Set<Integer>> likesMap = new HashMap<>();
        jdbc.query(getAllLikesQuery, rs -> {
            int filmId = rs.getInt("film_id");
            int userId = rs.getInt("user_id");
            likesMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        // Получаем все жанры
        String getAllGenresQuery = """
                SELECT fg.film_id, g.genre_id, g.name
                FROM films_genre fg
                JOIN genre g ON fg.genre_id = g.genre_id
                ORDER BY g.genre_id;
                """;
        Map<Integer, List<Genre>> genresMap = new HashMap<>();
        jdbc.query(getAllGenresQuery, rs -> {
            int filmId = rs.getInt("film_id");
            int genreId = rs.getInt("genre_id");
            String genreName = rs.getString("name");
            genresMap.computeIfAbsent(filmId, k -> new ArrayList<>())
                    .add(Genre.builder().id(genreId).name(genreName).build());
        });

        // Заполняем лайки и жанры для каждого фильма
        for (Film film : films) {
            int filmId = film.getId();
            if (likesMap.containsKey(filmId)) {
                film.getLikes().addAll(likesMap.get(filmId));
            }
            if (genresMap.containsKey(filmId)) {
                List<Genre> genres = genresMap.get(filmId);
                genres.sort(Comparator.comparingInt(Genre::getId));
                film.getGenres().addAll(genres);
            }
        }
        return films;
    }

    private void filmExists(int filmId) {
        String query = """
                SELECT COUNT(*)
                FROM films
                WHERE film_id = ?;
                """;
        Integer count = jdbc.queryForObject(query, Integer.class, filmId);
        if (!(count != null && count > 0)) {
            throw new NotFoundException("Film is not found",
                    new Throwable("Film ID: %d ;" + filmId).fillInStackTrace());
        }
    }

    private boolean filmValid(Film film) {
        if (film.getGenres() == null) {
            throw new InvalidInfoException("genres are invalid");
        }
        String queryGetAllMpa = """
                SELECT rating_id
                FROM mpa_rating;
                """;
        List<Integer> existingRatings = jdbc.query(queryGetAllMpa, (rs, rowNum) -> rs.getInt("rating_id"));

        String queryGetAllGenres = """
                SELECT genre_id
                FROM genre;
                """;
        List<Integer> existingGenres = jdbc.query(queryGetAllGenres, (rs, rowNum) -> rs.getInt("genre_id"));
        return existingRatings.contains(film.getMpa().getId())
                && new HashSet<>(existingGenres).containsAll(film.getGenres().stream().map(Genre::getId).toList());
    }
}
