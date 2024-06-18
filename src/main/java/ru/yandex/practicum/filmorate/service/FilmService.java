package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmIsNullException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorDbStorage directorStorage;

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
            return filmStorage.update(film);
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

    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        directorStorage.getDirector(directorId);
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> searchFilms(String query, List<String> by) {
        boolean byTitle = by.contains("title");
        boolean byDirector = by.contains("director");

        return filmStorage.searchFilms(query, byTitle, byDirector);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilmById(Long filmId) {
        filmStorage.deleteFilmById(filmId);
    }
}
