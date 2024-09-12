package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.util.List;

@Service
@Slf4j
public class FriendService {
    private final FriendStorage friendStorage;

    @Autowired
    public FriendService(FriendStorage friendStorage) {
        this.friendStorage = friendStorage;
    }

    public User addUserAsFriend(Integer userId, Integer userIdToAdd) {
        return friendStorage.addUserAsFriend(userId, userIdToAdd);
    }

    public User removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        return friendStorage.removeUserFromFriendsList(userId, userIdToRemove);
    }

    public List<User> getUserFriends(Integer userId) {
        return friendStorage.getUserFriends(userId);
    }

    public List<User> getMutualFriendsWithOtherUser(Integer userId, Integer otherUserId) {
        return friendStorage.getMutualFriendsWithOtherUser(userId, otherUserId);
    }
}
