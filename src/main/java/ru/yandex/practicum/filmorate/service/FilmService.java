package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmIsNullException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    @Qualifier(value = "filmDbStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

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

    public void addLike(long userId, long filmId) {
        filmStorage.addLike(userId, filmId);
    }

    public void deleteLike(long userId, long filmId) {
        filmStorage.deleteLike(userId, filmId);
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

    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        return    filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> searchFilms(String query, List<String> by) {
        boolean byTitle = by.contains("title");
        boolean byDirector = by.contains("director");

        return filmStorage.searchFilms(query, byTitle, byDirector);
    }

    public void deleteFilmById(Long filmId) {
        filmStorage.deleteFilmById(filmId);
    }
}
