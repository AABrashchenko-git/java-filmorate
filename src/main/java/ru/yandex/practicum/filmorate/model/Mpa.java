package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder(toBuilder = true)
@AllArgsConstructor
public class Mpa {
    private Integer id;
    @NotBlank(message = "MPA rating name should not be null")
    private String name;
}
