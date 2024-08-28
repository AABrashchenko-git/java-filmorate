package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.NoSpaces;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class User {
    private Integer id;
    @Email(message = "Invalid Email format, expected nickname@postservice.com")
    private String email;
    @NotEmpty(message = "login cannot be empty")
    @NoSpaces
    private String login;
    private String name;
    @Past(message = "incorrect birthday")
    private LocalDate birthday;
}
