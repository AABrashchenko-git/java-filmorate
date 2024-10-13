package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
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
        return addFriendsListToUsersCollection(users);
    }

    @Override
    public User get(Integer userId) {
        if (!userExists(userId)) {
            throw new NotFoundException(String.format("user with id = %d is not found", userId));
        }
        String getUserQuery = """
                SELECT *
                FROM users
                WHERE user_id = ?;
                """;
        User user = jdbc.queryForObject(getUserQuery, new Object[]{userId}, mapper);
        String geFriendsIdListQuery = """
                SELECT following_user_id
                FROM friendship
                WHERE followed_user_id = ? /*AND friendship_status = TRUE*/;
                """;
        // List<Integer> friendsId = jdbc.query(geFriendsIdListQuery, mapper, userId);
        Set<Integer> friendsIds = new HashSet<>(jdbc.queryForList(geFriendsIdListQuery, new Object[]{userId}, Integer.class));
        System.out.println("ВОТ ТУТОЧКИё" + friendsIds);
        user.getFriends().addAll(friendsIds);
        return user;
    }

    @Override
    public User add(User user) {
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
        return user;
    }

    @Override
    public User update(User updUser) {
        if (!userExists(updUser.getId())) {
            throw new NotFoundException("User is not found",
                    new Throwable("User ID: %d ;" + updUser.getId()).fillInStackTrace());
        }
        String query = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE user_id = ?;
                """;
        jdbc.update(query, updUser.getEmail(), updUser.getLogin(), updUser.getName(), Date.valueOf(updUser.getBirthday()), updUser.getId());
        return updUser;
    }

    @Override
    public void remove(Integer userId) {
        if (!userExists(userId)) {
            throw new NotFoundException("User is not found",
                    new Throwable("User ID: %d ;" + userId).fillInStackTrace());
        }
        //удалить пользователя
        String removeUserQuery = """
                DELETE FROM users
                WHERE user_id = ?;
                """;
        int rowsAffected = jdbc.update(removeUserQuery, userId);
        // удалить друзей - каскадное удаление при удалении пользователя
        if (rowsAffected == 0) {
            throw new IllegalStateException("Ошибка при удалении пользователя с id " + userId);
        }
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

    private boolean userExists(int userId) {
        String query = """
                SELECT COUNT(*)
                FROM users
                WHERE user_id = ?;
                """;
        Integer count = jdbc.queryForObject(query, Integer.class, userId);
        return count != null && count > 0;
    }


}
