package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder(toBuilder = true)
@AllArgsConstructor
public class Mpa {
   // @NotNull(message = "MPA Rating name should not be null")
    private Integer id;
   // @NotNull(message = "MPA Rating name should not be null")
    private String name;
}
