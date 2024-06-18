package ru.yandex.practicum.filmorate.exception.review;

public class ReviewLikeAlreadyExistsException extends RuntimeException {
    public ReviewLikeAlreadyExistsException(String message) {
        super(message);
    }
}
