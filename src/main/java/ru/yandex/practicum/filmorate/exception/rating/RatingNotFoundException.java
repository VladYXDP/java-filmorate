package ru.yandex.practicum.filmorate.exception.rating;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException(String message) {
        super(message);
    }
}
