package ru.yandex.practicum.filmorate.storage;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.DbFriendStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({DbFriendStorage.class, DbUserStorage.class, UserMapper.class})
public class DbFriendStorageTest {

    private final FriendStorage friendStorage;
    private final UserStorage userStorage;

    private User user1;
    private User user2;
    private User user3;

    @Autowired
    public DbFriendStorageTest(@Qualifier("dbFriendStorage") FriendStorage friendStorage, @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.friendStorage = friendStorage;
        this.userStorage = userStorage;
    }

    @BeforeEach
    void beforeEach() {
        // Создаем тестовых пользователей
        user1 = User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        user2 = User.builder()
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        user3 = User.builder()
                .email("user3@example.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(1985, 10, 10))
                .build();

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);
    }

    @Test
    void testAddUserAsFriendBothUsersAddEachOther() {
        // Пользователь 1 добавляет пользователя 2 в друзья
        friendStorage.addUserAsFriend(user1.getId(), user2.getId());
        // Пользователь 2 добавляет пользователя 1 в друзья
        friendStorage.addUserAsFriend(user2.getId(), user1.getId());

        // Проверяем, что оба пользователя стали друзьями
        User updatedUser1 = userStorage.get(user1.getId());
        User updatedUser2 = userStorage.get(user2.getId());

        assertThat(updatedUser1.getFriends()).contains(user2.getId());
        assertThat(updatedUser2.getFriends()).contains(user1.getId());

    }


    @Test
    void testRemoveUserFromFriendsList() {
        friendStorage.addUserAsFriend(user1.getId(), user2.getId());
        friendStorage.addUserAsFriend(user2.getId(), user1.getId());


        // Удаляем пользователя 2 из друзей пользователя 1
        friendStorage.removeUserFromFriendsList(user1.getId(), user2.getId());

        // Проверяем, что пользователь 2 удален из друзей пользователя 1
        User userAfterRemove = userStorage.get(user1.getId());
        assertThat(userAfterRemove.getFriends()).doesNotContain(user2.getId());
    }

    @Test
    void testGetUserFriends() {
        friendStorage.addUserAsFriend(user1.getId(), user2.getId());
        friendStorage.addUserAsFriend(user2.getId(), user1.getId());

        friendStorage.addUserAsFriend(user1.getId(), user3.getId());
        friendStorage.addUserAsFriend(user3.getId(), user1.getId());

        // Получаем друзей пользователя 1
        List<User> friends = friendStorage.getUserFriends(user1.getId());

        // Проверяем, что друзья пользователя 1 содержат пользователей 2 и 3
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getId).contains(user2.getId(), user3.getId());
    }

    @Test
    void testGetMutualFriendsWithOtherUser() {
        friendStorage.addUserAsFriend(user1.getId(), user2.getId());
        friendStorage.addUserAsFriend(user2.getId(), user1.getId());

        friendStorage.addUserAsFriend(user1.getId(), user3.getId());
        friendStorage.addUserAsFriend(user3.getId(), user1.getId());


        friendStorage.addUserAsFriend(user2.getId(), user3.getId());
        friendStorage.addUserAsFriend(user3.getId(), user2.getId());

        // Получаем общих друзей пользователя 1 и пользователя 2
        List<User> mutualFriends = friendStorage.getMutualFriendsWithOtherUser(user1.getId(), user2.getId());

        // Проверяем, что общий друг - пользователь 3
        assertThat(mutualFriends).hasSize(1);
        assertThat(mutualFriends.get(0).getId()).isEqualTo(user3.getId());
    }

    @Test
    void testRemoveUserFromFriendsListThrowsNotFoundException() {
        // Проверяем, что метод выбрасывает исключение, если пользователь не найден
        assertThrows(NotFoundException.class, () -> friendStorage.removeUserFromFriendsList(999, user1.getId()));
        assertThrows(NotFoundException.class, () -> friendStorage.removeUserFromFriendsList(user1.getId(), 999));
    }

}