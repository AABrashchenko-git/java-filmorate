package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class InMemoryFriendStorage implements FriendStorage {

    private final UserStorage userStorage;

    public InMemoryFriendStorage(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addUserAsFriend(Integer userId, Integer userIdToAdd) {
        userStorage.get(userId).getFriends().add(userIdToAdd);
        userStorage.get(userIdToAdd).getFriends().add(userId);
        log.info("User {} added user {} to friends list", userId, userIdToAdd);
        userStorage.get(userIdToAdd);
    }

    @Override
    public void removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        userStorage.get(userId).getFriends().remove(userIdToRemove);
        userStorage.get(userIdToRemove).getFriends().remove(userId);
        log.info("User {} removed user {} from friends list", userId, userIdToRemove);
        userStorage.get(userIdToRemove);
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        List<User> friends = userStorage.get(userId).getFriends()
                .stream().map(userStorage::get).toList();
        log.info("get user {} friends handled", userId);
        return friends;
    }

    @Override
    public List<User> getMutualFriendsWithOtherUser(Integer userId, Integer otherUserId) {
        Set<Integer> userFriends = userStorage.get(userId).getFriends();
        Set<Integer> otherUserFriends = userStorage.get(otherUserId).getFriends();
        log.info("Mutual friends of users {} and {} handled", userId, otherUserId);
        return userFriends
                .stream().filter(otherUserFriends::contains)
                .map(userStorage::get).toList();
    }

}
