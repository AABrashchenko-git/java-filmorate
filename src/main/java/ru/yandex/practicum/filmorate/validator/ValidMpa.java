package ru.yandex.practicum.filmorate.validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MpaValidator.class)
public @interface ValidMpa {
    String message() default "Invalid MPA rating";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}