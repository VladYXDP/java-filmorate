package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmIsNullException;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
        Film film = filmStorage.get(filmId);
        userStorage.get(userId);
        film.addLike(userId);
        filmStorage.update(film);
    }

    public void deleteLike(long userId, long filmId) {
        Film film = filmStorage.get(filmId);
        if (film.getLikes().contains(userId) && userStorage.get(userId) != null) {
            film.deleteLike(userId);
            filmStorage.update(film);
        } else {
            throw new UserNotFoundException(String.format("Не удалось удалить лайк пользователя с id %d", userId));
        }
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.get(id);
    }

    public List<Film> getPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
