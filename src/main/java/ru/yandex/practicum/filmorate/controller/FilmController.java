package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDtoMapper;
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
        Film film = filmService.addFilm(filmDtoTransfer.dtoToFilm(filmDto));
        log.info(String.format("Фильм %s успешно добавлен!", film.getName()));
        return filmDtoTransfer.filmToDto(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = filmService.updateFilm(filmDtoTransfer.dtoToFilm(filmDto));
        log.info(String.format("Фильм %s успешно обновлён!", film.getName()));
        return filmDtoTransfer.filmToDto(film);
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable long id) {
        log.info(String.format("Получить фильм с %d ", id));
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
        log.info(String.format("Пользователь с %d поставил лайк фильму с %d", userId, id));
        filmService.likeFilm(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("Пользователь с %d удаляет лайк с фильма с %d", userId, id));
        filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info(String.format("Вывод %d популярных фильмов", count));
        return filmService.getPopularFilms(count).stream()
                .map(filmDtoTransfer::filmToDto)
                .collect(Collectors.toList());
    }
}