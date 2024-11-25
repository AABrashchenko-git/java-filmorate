package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
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
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Login cannot be empty")
    @NoSpaces
    private String login;
    private String name;
    @PastOrPresent(message = "Incorrect birthday")
    private LocalDate birthday;
}
