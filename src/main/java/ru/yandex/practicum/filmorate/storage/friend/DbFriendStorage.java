package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Repository("dbFriendStorage")
public class DbFriendStorage implements FriendStorage {
    private final JdbcTemplate jdbc;
    private final UserMapper mapper; /*TODO а юзер ли здесь? */
    private final UserStorage userStorage;

    public DbFriendStorage(JdbcTemplate jdbc, UserMapper mapper,  @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.userStorage = userStorage;
    }

    @Override
    public void addUserAsFriend(Integer userId, Integer friendIdToAdd) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendIdToAdd);
        String addRelationQuery = """
                INSERT INTO friendship (followed_user_id, following_user_id, friendship_status)
                VALUES (?, ?, ?);
                """;
        String updateRelationQuery = """
                UPDATE friendship
                SET friendship_status = true
                WHERE followed_user_id = ? AND following_user_id = ?;
                """;
       if(!user.getFriends().contains(friendIdToAdd) && !friend.getFriends().contains(userId)) {
           jdbc.update(addRelationQuery, userId, friendIdToAdd, true);
           jdbc.update(addRelationQuery, friendIdToAdd, userId, false);
        } else if(!user.getFriends().contains(friendIdToAdd) && friend.getFriends().contains(friendIdToAdd)) {
           jdbc.update(updateRelationQuery , friendIdToAdd, userId);

       } else if (user.getFriends().contains(friendIdToAdd) && !friend.getFriends().contains(friendIdToAdd)) {
           jdbc.update(updateRelationQuery , userId, friendIdToAdd);
       }
    }

    @Override
    public void removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        if (!userExists(userId) || !userExists(userIdToRemove) ) {
            throw new NotFoundException("User with id " + userId + " or " + userIdToRemove + " does not exist");
        }
        String query = """
                DELETE FROM friendship
                WHERE followed_user_id = ? AND following_user_id = ?;
                """;
        jdbc.update(query, userId, userIdToRemove);
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        if (!userExists(userId)) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friendship f ON u.user_id = f.following_user_id
                WHERE f.followed_user_id = ?
                AND f.friendship_status = TRUE;
                """;

        List<User> friends = jdbc.query(sql, mapper, userId);
        // Загрузка всех друзей одним запросом
        Map<Integer, Set<Integer>> friendsMap = loadFriendsMap(userId);
        // Заполнение сетов друзей для каждого пользователя
        for (User friend : friends) {
            friend.getFriends().addAll(friendsMap.getOrDefault(friend.getId(), new HashSet<>()));
        }
        return friends;
    }

    @Override
    public List<User> getMutualFriendsWithOtherUser(Integer userId, Integer otherUserId) {
        String query = """
                SELECT u.*
                FROM users AS u
                WHERE u.user_id IN (
                    SELECT f1.following_user_id
                    FROM friendship AS f1
                    WHERE f1.followed_user_id = ? AND f1.friendship_status = true
                )
                AND u.user_id IN (
                    SELECT f2.following_user_id
                    FROM friendship AS f2
                    WHERE f2.followed_user_id = ? AND f2.friendship_status = true
                );
                """;
        return jdbc.query(query, mapper, userId, otherUserId);
    }

    private boolean relationExists(int followedUserId, int followingUserId) {
        String query = """
                SELECT COUNT(*)
                FROM users
                WHERE user_id = ? OR user_id = ?;
                """;
        Integer count = jdbc.queryForObject(query, Integer.class, followedUserId, followingUserId);
        return !(count == null || count <= 1);
    }

    private Map<Integer, Set<Integer>> loadFriendsMap(Integer userId) {
        String friendsQuery = """
                SELECT followed_user_id, following_user_id
                FROM friendship
                WHERE followed_user_id IN (SELECT following_user_id FROM friendship WHERE followed_user_id = ?)
                AND friendship_status = TRUE;
                """;

        return jdbc.query(friendsQuery, rs -> {
            Map<Integer, Set<Integer>> friendsMap = new HashMap<>();
            while (rs.next()) {
                int followedUserId = rs.getInt("followed_user_id");
                int followingUserId = rs.getInt("following_user_id");
                friendsMap.computeIfAbsent(followedUserId, k -> new HashSet<>()).add(followingUserId);
            }
            return friendsMap;
        }, userId);
    }

    private boolean userExists(Integer userId) {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE user_id = ?;
                """;
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}
