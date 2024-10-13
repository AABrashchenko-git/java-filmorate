package ru.yandex.practicum.filmorate.validator;
import ru.yandex.practicum.filmorate.model.Mpa;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MpaValidator implements ConstraintValidator<ValidMpa, Mpa> {

    @Override
    public void initialize(ValidMpa constraintAnnotation) {
    }

    @Override
    public boolean isValid(Mpa mpa, ConstraintValidatorContext context) {
        return mpa != null && mpa.getId() != null;
    }
}