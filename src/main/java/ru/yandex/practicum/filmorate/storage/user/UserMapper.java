package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder().id(rs.getInt("user_id"))
                .email(rs.getString("email")).login(rs.getString("login"))
                .name(rs.getString("name")).birthday(rs.getDate("birthday").toLocalDate())
                .likedFilms(new HashSet<>())
                .friends(new HashSet<>()).build();
    }
}