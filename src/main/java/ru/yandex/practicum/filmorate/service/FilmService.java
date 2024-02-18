package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmIsNullException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addFilm(Film film) {
        if (film != null) {
            filmStorage.add(film);
            return film;
        } else {
            throw new FilmIsNullException("Ошибка добавления фильма!");
        }
    }

    public Film updateFilm(Film film) {
        if (film != null) {
            filmStorage.update(film);
            return film;
        } else {
            throw new FilmIsNullException("Ошибка обновления фильма!");
        }
    }

    public Film deleteFilm(Film film) {
        if (film != null) {
            return filmStorage.delete(film);
        } else {
            throw new UserIsNullException("Ошибка удаления фильма!");
        }
    }

    public void likeFilm(long userId, long filmId) {
        if (filmStorage.getAllFilms().containsKey(filmId)) {
            userService.getUserById(userId);
            filmStorage.getAllFilms().get(filmId).addLike(userId);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден!", filmId));
        }
    }

    public void deleteLike(long userId, long filmId) {
        if (filmStorage.getAllFilms().containsKey(filmId)) {
            if (filmStorage.getAllFilms().get(filmId).getLikes().contains(userId) || userService.getUserById(userId) == null) {
                filmStorage.getAllFilms().get(filmId).deleteLike(userId);
            } else {
                throw new UserNotFoundException(String.format("Не удалось удалить лайк пользователя с id %d", userId));
            }
        } else {
            throw new FilmNotFoundException(String.format("Ошибка удаления лайка! Фильм с id %d не найден!", filmId));
        }
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getAllFilms().values());
    }

    public Film getFilmById(long id) {
        if (filmStorage.getAllFilms().containsKey(id)) {
            return filmStorage.getAllFilms().get(id);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден!", id));
        }
    }

    public List<Film> getPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
