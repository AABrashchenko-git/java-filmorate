package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User get(Integer userId);

    User add(User user);

    User update(User updUser);

    User remove(Integer id);
}
