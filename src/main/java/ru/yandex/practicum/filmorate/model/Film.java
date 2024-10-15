package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Integer id;
    @NotBlank(message = "Film name should not be empty")
    private String name;
    @NotBlank(message = "Description should not be empty")
    @Size(max = 200, message = "Size should be less than 200 symbols")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Duration should be positive")
    @NotNull(message = "Duration should not be empty")
    private Integer duration;
    private final Set<Genre> genres = new LinkedHashSet<>();
    @NotNull
    private Mpa mpa;

}
