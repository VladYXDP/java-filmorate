package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDtoMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final FilmDtoMapper filmDtoTransfer;

    @PostMapping
    public FilmDto addFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film;
        if (filmDto != null && !filmDto.getDuration().isNegative()) {
            film = filmService.addFilm(filmDtoTransfer.dtoToFilm(filmDto));
            log.info("Фильм " + film.getName() + "успешно добавлен!");
        } else {
            throw new ValidationException("Продолжительность фильма не должна быть меньше нуля!");
        }
        return filmDtoTransfer.filmToDto(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = filmService.updateFilm(filmDtoTransfer.dtoToFilm(filmDto));
        log.info("Фильм " + film.getName() + " успешно обновлён!");
        return filmDtoTransfer.filmToDto(film);
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable long id) {
        log.info("Получить фильм с id " + id);
        return filmDtoTransfer.filmToDto(filmService.getFilmById(id));
    }

    @GetMapping
    public List<FilmDto> getAllFilms() {
        log.info("Получен список всех фильмов!");
        return filmService.getAllFilms()
                .stream()
                .map(filmDtoTransfer::filmToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь с id " + userId + " поставил лайк фильму с id " + id);
        filmService.likeFilm(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь с id " + userId + " удаляет лайк с фильма с id " + id);
        filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Вывод " + count + " популярных фильмов");
        return filmService.getPopularFilms(count).stream()
                .map(filmDtoTransfer::filmToDto)
                .collect(Collectors.toList());
    }
}