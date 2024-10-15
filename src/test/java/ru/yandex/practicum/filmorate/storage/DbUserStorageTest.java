package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({DbUserStorage.class, UserMapper.class})
public class DbUserStorageTest {
    private final UserStorage userStorage;
    private User user1;
    private User user2;

    @Autowired
    public DbUserStorageTest(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @BeforeEach
    void beforeEach() {
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

        userStorage.addUser(user1);
        userStorage.addUser(user2);
    }

    @Test
    void testGetUserById() {
        User receivedUser1 = userStorage.getUser(1);
        User receivedUser2 = userStorage.getUser(2);
        assertThat(receivedUser1).isNotNull();
        assertThat(receivedUser1.getId()).isEqualTo(1);
        assertThat(receivedUser2).isNotNull();
        assertThat(receivedUser2.getId()).isEqualTo(2);
    }

    @Test
    void testGetAllUsers() {
        Collection<User> users = userStorage.getAllUsers();
        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(2);
    }

    @Test
    void testUpdateUser() {
        User existingUser = userStorage.getUser(1);
        existingUser.setName("Updated User");
        existingUser.setEmail("updated@example.com");

        User updatedUser = userStorage.updateUser(existingUser);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testRemoveUser() {
        userStorage.removeUser(1);
        assertThrows(NotFoundException.class, () -> userStorage.getUser(1));
    }
}

