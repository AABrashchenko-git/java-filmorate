package ru.yandex.practicum.filmorate.exception;


public class InvalidInfoException extends RuntimeException {
    public InvalidInfoException(String message) {
        super(message);
    }

    public InvalidInfoException(String message, Throwable cause) {
        super(message, cause);
    }

}