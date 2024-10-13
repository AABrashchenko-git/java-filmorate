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
    private Integer id;
    private String name;
}
