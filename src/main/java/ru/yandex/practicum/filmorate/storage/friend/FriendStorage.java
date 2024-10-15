package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addUserAsFriend(Integer userId, Integer userIdToAdd);

    void removeUserFromFriendsList(Integer userId, Integer userIdToRemove);

    List<User> getUserFriends(Integer userId);

    List<User> getMutualFriendsWithOtherUser(Integer userId, Integer otherUserId);

}
