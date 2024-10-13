package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Film film1;
    private Film film2;

    @BeforeEach
    void beforeEach() {
        // проходят по критериям валидации
        Set<Genre> testGenres1 = new HashSet<>();
        testGenres1.add(new Genre(1, "Комедия"));
        testGenres1.add(new Genre(2, "Драма"));

        Set<Genre> testGenres2 = new HashSet<>();
        testGenres2.add(new Genre(2, "Драма"));

        film1 = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .build();
        film2 = Film.builder()
                .name("film2Name")
                .description("film2Description")
                .duration(120)
                .releaseDate(LocalDate.now().minusYears(20))
                .mpa(new Mpa(2, "PG"))
                .build();
        film1.getGenres().addAll(testGenres1);
        film2.getGenres().addAll(testGenres2);

    }

    @Test
    void testAddValidFilmsAndGetAll() throws Exception {
        // Добавляем валидные фильмы и получаем их
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddInvalidTimeStamps() throws Exception {
        // Добавляем фильм с невалидной датой релиза, ожидаем ошибку 400
        film1 = film1.toBuilder().releaseDate(LocalDate.now().minusYears(200)).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isBadRequest());
        // а затем второй с отрицательной продолжительностью
        film2 = film2.toBuilder().duration(-100).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddInvalidTextInfo() throws Exception {
        // пустое имя
        film1 = film1.toBuilder().name("").build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isBadRequest());
        // описание более 200 символов
        film2 = film2.toBuilder().description("badRequest".repeat(21)).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSuccessfulUpdate() throws Exception {
        // постим фильм, затем обновляем
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film1)));
        film1 = film1.toBuilder().id(1).description("film1NEWDescription").build();

        MvcResult result = mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk())
                .andReturn();
        // Сравниваем возвращенный ответ сервера с обновленным фильмом с фильмом из запроса на обновление
        String responseBody = result.getResponse().getContentAsString();
        Film responseFilm = objectMapper.readValue(responseBody, Film.class);

        assertEquals(film1, responseFilm);
    }

    @Test
    void testAddInvalidRequestUpdate() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film1)));
        // пытаемся обновить несуществующий фильм, ожидаем 404
        film1 = film1.toBuilder().id(999).build();
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isNotFound());
    }
}
