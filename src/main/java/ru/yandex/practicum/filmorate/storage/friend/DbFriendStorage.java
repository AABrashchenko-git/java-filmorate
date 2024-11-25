package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;

import java.util.*;

@Slf4j
@Repository("dbFriendStorage")
public class DbFriendStorage implements FriendStorage {
    private final JdbcTemplate jdbc;
    private final UserMapper mapper;

    public DbFriendStorage(JdbcTemplate jdbc, UserMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public void addUserAsFriend(Integer userId, Integer friendIdToAdd) {
        List<Integer> userFriendsIds = getUserFriends(userId).stream().map(User::getId).toList();
        List<Integer> userToAddFriendsIds = getUserFriends(friendIdToAdd).stream().map(User::getId).toList();

        String addRelationQuery = """
                INSERT INTO friendship (followed_user_id, following_user_id, friendship_status)
                VALUES (?, ?, ?);
                """;
        String updateRelationQuery = """
                UPDATE friendship
                SET friendship_status = true
                WHERE followed_user_id = ? AND following_user_id = ?;
                """;
        if (!userFriendsIds.contains(friendIdToAdd) && !userToAddFriendsIds.contains(userId)) {
            jdbc.update(addRelationQuery, userId, friendIdToAdd, true);
            jdbc.update(addRelationQuery, friendIdToAdd, userId, false);
        } else if (!userFriendsIds.contains(friendIdToAdd) && userToAddFriendsIds.contains(userId)) {

            jdbc.update(updateRelationQuery, userId, friendIdToAdd);
        } else if (userFriendsIds.contains(friendIdToAdd) && !userToAddFriendsIds.contains(userId)) {
            jdbc.update(updateRelationQuery, friendIdToAdd, userId);
        }
        log.info("User {} added user {} to friends list", userId, friendIdToAdd);
    }

    @Override
    public void removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        userExists(userId);
        userExists(userIdToRemove);
        String query = """
                DELETE FROM friendship
                WHERE followed_user_id = ? AND following_user_id = ?;
                """;
        jdbc.update(query, userId, userIdToRemove);
        log.info("User {} removed user {} from friends list", userId, userIdToRemove);
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        userExists(userId);
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friendship f ON u.user_id = f.following_user_id
                WHERE f.followed_user_id = ?
                AND f.friendship_status = TRUE;
                """;

        List<User> friends = jdbc.query(sql, mapper, userId);

        log.info("get user {} friends handled", userId);
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
        log.info("Mutual friends of users {} and {} handled", userId, otherUserId);
        return jdbc.query(query, mapper, userId, otherUserId);
    }

    private void userExists(Integer userId) {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE user_id = ?;
                """;
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        if (!(count != null && count > 0)) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
    }
}
