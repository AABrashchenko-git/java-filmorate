package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class InMemoryFriendStorage implements FriendStorage {

    private final UserStorage userStorage;

    @Autowired
    public InMemoryFriendStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addUserAsFriend(Integer userId, Integer userIdToAdd) {
        User actualUser = userStorage.get(userId);
        User userToFriend = userStorage.get(userIdToAdd);
        actualUser.getFriends().add(userToFriend.getId());
        userToFriend.getFriends().add(actualUser.getId());
        log.info("User {} added user {} to friends list", userId, userIdToAdd);
        return userToFriend;
    }

    @Override
    public User removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        User actualUser = userStorage.get(userId);
        User userToRemove = userStorage.get(userIdToRemove);
        actualUser.getFriends().remove(userToRemove.getId());
        userToRemove.getFriends().remove(actualUser.getId());
        log.info("User {} removed user {} from friends list", userId, userIdToRemove);
        return userToRemove;
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
