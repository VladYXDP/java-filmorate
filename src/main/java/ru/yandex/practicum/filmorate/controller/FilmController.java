package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        try {
            filmService.addFilm(film);
            log.info("Фильм " + film.getName() + "успешно добавлен!");
        } catch (ValidationException e) {
            log.error("Ошибка валидации! " + e.getMessage());
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            filmService.updateFilm(film);
            log.info("Фильм " + film.getName() + " успешно обновлён!");
        } catch (ValidationException e) {
            log.error("Ошибка валидации! " + e.getMessage());
        }
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }
}