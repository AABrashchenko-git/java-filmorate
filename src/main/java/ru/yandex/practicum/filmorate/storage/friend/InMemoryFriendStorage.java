package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

/**
 * Как ты посоветовал, сделал отдельные сервисы и хранилища для друзей и лайков
 * Понятно, что мы это делаем, чтобы разграничить функциональность разных частей приложения,
 * но не странно, что мы делаем хранилище друзей, в конструктор которого внедряется зависимость
 * от хранилища пользователей (при этом никакой коллекции, мапы или сета мы тут не создаем, а используем мапу
 * их хранилища пользователей)? Это нормальная практика? Аналогично с хранилищем лайков
 * Просто выглядит так, будто мы плодим лишние сущности, и лучше все просто сделать внутри UserStorage :)
 *
 * (либо другой вариант - создать тут отдельную структуру данных для хранения лайков и друзей, но это ещё более
 * странно,ведь каждый фильм уже хранит в себе рейтинг, а каждый пользователь - список друзей)
 */
@Component
@Slf4j
public class InMemoryFriendStorage implements FriendStorage {

    private final UserStorage userStorage;

    @Autowired
    public InMemoryFriendStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUserAsFriend(Integer userId, Integer userIdToAdd) {
        User actualUser = userStorage.get(userId);
        User userToFriend = userStorage.get(userIdToAdd);
        actualUser.getFriends().add(userToFriend.getId());
        userToFriend.getFriends().add(actualUser.getId());
        log.info("User {} added user {} to friends list", userId, userIdToAdd);
        return userToFriend;
    }

    public User removeUserFromFriendsList(Integer userId, Integer userIdToRemove) {
        User actualUser = userStorage.get(userId);
        User userToRemove = userStorage.get(userIdToRemove);
        actualUser.getFriends().remove(userToRemove.getId());
        userToRemove.getFriends().remove(actualUser.getId());
        log.info("User {} removed user {} from friends list", userId, userIdToRemove);
        return userToRemove;
    }

    public List<User> getUserFriends(Integer userId) {
        List<User> friends = userStorage.get(userId).getFriends()
                .stream().map(userStorage::get).toList();
        log.info("get user {} friends handled", userId);
        return friends;
    }

    public List<User> getMutualFriendsWithOtherUser(Integer userId, Integer otherUserId) {
        Set<Integer> userFriends = userStorage.get(userId).getFriends();
        Set<Integer> otherUserFriends = userStorage.get(otherUserId).getFriends();
        log.info("Mutual friends of users {} and {} handled", userId, otherUserId);
        return userFriends
                .stream().filter(otherUserFriends::contains)
                .map(userStorage::get).toList();
    }


}
