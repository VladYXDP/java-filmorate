package ru.yandex.practicum.filmorate.exception.review;

public class ReviewLikeNotFoundException extends RuntimeException{
    public ReviewLikeNotFoundException(String message) {
        super(message);
    }
}
