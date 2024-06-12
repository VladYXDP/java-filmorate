package ru.yandex.practicum.filmorate.exception.film;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SortException extends RuntimeException {

    private static final String message = "Некорректные параметры сортировки";

    public SortException() {
        super(message);
    }
}
