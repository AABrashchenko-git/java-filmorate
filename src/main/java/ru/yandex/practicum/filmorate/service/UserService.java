package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User updUser) {
        return userStorage.update(updUser);
    }

    public User getUser(int id) {
        return userStorage.get(id);
    }

    public User removeUser(int userId) {
        return userStorage.remove(userId);
    }

    public User addUserAsFriend(Integer userId, Integer userIdToAdd) {
        User actualUser = userStorage.get(userId);
        User userToFriend = userStorage.get(userIdToAdd);
        actualUser.getFriends().add(userToFriend.getId());
        userToFriend.getFriends().add(actualUser.getId());
        return userToFriend;
    }

    public User removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        User actualUser = userStorage.get(userId);
        User userToRemove = userStorage.get(userIdToRemove);
        actualUser.getFriends().remove(userToRemove.getId());
        userToRemove.getFriends().remove(actualUser.getId());
        return userToRemove;
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.get(userId).getFriends()
                .stream().map(userStorage::get).collect(Collectors.toList());
    }

    public List<User> getMutualFriendsWithOtherUser(Integer userId, Integer otherUserId) {
        Set<Integer> userFriends = userStorage.get(userId).getFriends(); // друзья данного юзера
        Set<Integer> otherUserFriends = userStorage.get(otherUserId).getFriends(); // друзья другого юзера

        return userFriends
                .stream().filter(otherUserFriends::contains)
                .map(userStorage::get).toList();
    }

}
