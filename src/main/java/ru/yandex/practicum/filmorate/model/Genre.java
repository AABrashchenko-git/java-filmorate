package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Genre {
    @NotNull(message = "Genre id should not be null")
    private Integer id;
    @NotNull(message = "Genre name should not be null")
    private String name;
}
