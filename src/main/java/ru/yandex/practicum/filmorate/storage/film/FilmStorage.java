package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Film add(Film film);

    Film delete(Film film);

    Film update(Film film);

    Film get(long filmId);

    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    List<Film> getAllFilms();

    List<Film> getDirectorFilms(long directorId, String sortBy);

    List<Film> searchFilms(String query, boolean byTitle, boolean byDirector);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getPopularFilms(int count, Integer genreId, Integer year);
}
