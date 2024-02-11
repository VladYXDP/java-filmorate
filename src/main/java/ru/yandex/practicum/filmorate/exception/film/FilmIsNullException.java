package ru.yandex.practicum.filmorate.exception.film;

public class FilmIsNullException extends RuntimeException {

    public FilmIsNullException(String message) {
        super(message);
    }
}
