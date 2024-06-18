package ru.yandex.practicum.filmorate.exception.review;

public class ReviewDislikeAlreadyExistsException extends RuntimeException {
    public ReviewDislikeAlreadyExistsException(String message) {
        super(message);
    }
}
