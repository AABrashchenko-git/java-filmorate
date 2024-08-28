package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * В доп. задании к ТЗ указано, что для валидации можно использовать аннотацию @Valid, в попытках разобраться
 * я ушел в гугл и нашел инфу о глобальном обработчике исключений, который
 * перехватывает MethodArgumentNotValidException в методах контроллеров и отправляет ответ клиенту
 * Не знаю, насколько это корректный подход в данном ТЗ, но решил попробовать,
 * вместо того, чтобы писать дополнительные методы для валидации и выбрасывать исключения
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Validation failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundExceptions(NotFoundException ex) {
        log.warn("Processing failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("[]");
    }
}
