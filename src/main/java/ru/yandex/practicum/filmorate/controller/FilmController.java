package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDtoMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.Collections;
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
    public FilmDto addFilm(@Valid @RequestBody final FilmDto filmDto) {
        Film film;
        if (filmDto != null && filmDto.getDuration() >= 0) {
            film = filmService.addFilm(filmDtoTransfer.dtoToFilm(filmDto));
            log.info(String.format("Фильм %s успешно добавлен!", film.getName()));
        } else {
            throw new ValidationException("Продолжительность фильма не должна быть меньше нуля!");
        }
        return filmDtoTransfer.filmToDto(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody final FilmDto filmDto) {
        Film film = filmService.updateFilm(filmDtoTransfer.dtoToFilm(filmDto));
        log.info(String.format("Фильм %s успешно обновлён!", film.getName()));
        return filmDtoTransfer.filmToDto(film);
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@Positive(message = "id фильма должен быть больше 0") @PathVariable long id) {
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
    public void likeFilm(@Positive(message = "id фильма должен быть больше 0") @PathVariable long id,
                         @Positive(message = "id пользователя должен быть больше 0") @PathVariable long userId) {
        log.info(String.format("Пользователь с %d поставил лайк фильму с %d", userId, id));
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Positive(message = "id фильма должен быть больше 0") @PathVariable long id,
                           @Positive(message = "id пользователя должен быть больше 0") @PathVariable long userId) {
        log.info(String.format("Пользователь с %d удаляет лайк с фильма с %d", userId, id));
        filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(
            @Positive(message = "Количество фильмов должно быть больше 0") @RequestParam(required = false, defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer genreId, @RequestParam(required = false) Integer year) {
        log.info(String.format("Вывод %d популярных фильмов", count));
        return filmService.getPopularFilms(count, genreId, year).stream()
                .map(filmDtoTransfer::filmToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        log.info("Поступил запрос на получение фильмов режиссера с id {}", directorId);
        List<FilmDto> films = filmService.getDirectorFilms(directorId, sortBy).stream().map(filmDtoTransfer::filmToDto)
                .collect(Collectors.toList());
        log.debug("Фильмы получены {}", films);
        return films;
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam(required = false) String query, @RequestParam(required = false) String by) {
        List<String> byList = by != null ? Arrays.asList(by.split(",")) : Collections.emptyList();
        log.info("Поступил запрос на получение фильмов по запросу: {}", byList);
        if (query == null || query.trim().isEmpty()) {
            log.info("Запрос пустой, возвращаем пустой список фильмов");
            return Collections.emptyList();
        }
        List<FilmDto> films = filmService.searchFilms(query, byList)
                .stream()
                .map(filmDtoTransfer::filmToDto)
                .collect(Collectors.toList());
        log.info("Фильмы найдены {}", films);
        return films;
    }

    @GetMapping("/common")
    public List<Film> getCommonsFilms(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "friendId") Long friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        filmService.deleteFilmById(filmId);
    }
}