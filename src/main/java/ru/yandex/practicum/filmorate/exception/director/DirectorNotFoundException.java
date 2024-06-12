package ru.yandex.practicum.filmorate.exception.director;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DirectorNotFoundException extends RuntimeException {

    public DirectorNotFoundException(String message) {
        super(message);
    }
}
