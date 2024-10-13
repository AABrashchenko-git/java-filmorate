package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Repository("dbUserStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserMapper mapper;

    @Override
    public Collection<User> getAll() {
        String query = """
                SELECT * FROM users;
                """;
        Collection<User> users = jdbc.query(query, mapper);
        log.info("/users get all users handled");
        return addFriendsListToUsersCollection(users);
    }

    @Override
    public User get(Integer userId) {
        userExists(userId);
        String getUserQuery = """
                SELECT *
                FROM users
                WHERE user_id = ?;
                """;
        User user = jdbc.queryForObject(getUserQuery, mapper, userId);
        String geFriendsIdListQuery = """
                SELECT following_user_id
                FROM friendship
                WHERE followed_user_id = ? /*AND friendship_status = TRUE*/;
                """;
        Set<Integer> friendsIds = new HashSet<>(jdbc.queryForList(geFriendsIdListQuery, new Object[]{userId}, Integer.class));
        if(user != null){
            user.getFriends().addAll(friendsIds);
        }
        log.info("get /users/{} handled", userId);
        return user;
    }

    @Override
    public User add(User user) {
        user.setName(getDisplayedName(user));
        String addUserQuery = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(addUserQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("User {} is created", user.getLogin());
        return user;
    }

    @Override
    public User update(User updUser) {
        userExists(updUser.getId());
        String query = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE user_id = ?;
                """;
        jdbc.update(query, updUser.getEmail(), updUser.getLogin(), updUser.getName(), Date.valueOf(updUser.getBirthday()), updUser.getId());
        log.info("User {} is updated: {}", updUser.getLogin(), updUser);
        return updUser;
    }

    @Override
    public void remove(Integer userId) {
        userExists(userId);
        String removeUserQuery = """
                DELETE FROM users
                WHERE user_id = ?;
                """;
        int rowsAffected = jdbc.update(removeUserQuery, userId);
        if (rowsAffected == 0) {
            throw new IllegalStateException("Ошибка при удалении пользователя с id " + userId);
        }
        log.info("User with id = {} is removed", userId);
    }

    private Collection<User> addFriendsListToUsersCollection(Collection<User> usersWithoutFriends) {
        String query = """
                SELECT followed_user_id, following_user_id
                FROM friendship
                WHERE friendship_status = TRUE;
                """;
        Map<Integer, Set<Integer>> likedFilms = new HashMap<>();
        jdbc.query(query, rs -> {
            Integer followed_user_id = rs.getInt("followed_user_id");
            Integer following_user_id = rs.getInt("following_user_id");
            likedFilms.computeIfAbsent(followed_user_id, k -> new HashSet<>()).add(following_user_id);
        });
        usersWithoutFriends.forEach(u -> {
            if (likedFilms.containsKey(u.getId())) {
                u.getLikedFilms().addAll(likedFilms.get(u.getId()));
            }
        });
        return usersWithoutFriends;
    }

    private void userExists(int userId) {
        String query = """
                SELECT COUNT(*)
                FROM users
                WHERE user_id = ?;
                """;
        Integer count = jdbc.queryForObject(query, Integer.class, userId);
        if (!(count != null && count > 0)) {
            throw new NotFoundException(String.format("user with id = %d is not found", userId));
        }
    }

    private String getDisplayedName(User user) {
        return user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getLogin();
    }
}
