package ru.yandex.practicum.filmorate.exception.user;

public class UserIsNullException extends RuntimeException {

    public UserIsNullException(String message) {
        super(message);
    }
}
