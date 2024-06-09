package ru.yandex.practicum.filmorate.exception.review;

public class ReviewDislikeNotFoundException extends RuntimeException{
    public ReviewDislikeNotFoundException(String message) {
        super(message);
    }
}
