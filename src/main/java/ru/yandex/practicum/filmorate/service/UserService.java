package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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

}
