package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Integer id;
    @NotEmpty(message = "Film name should not be empty")
    private String name;
    @NotEmpty(message = "Description should not be empty")
    @Size(max = 200, message = "size should be less than 200 symbols")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive (message = "duration should be positive")
    private Integer duration;
}
