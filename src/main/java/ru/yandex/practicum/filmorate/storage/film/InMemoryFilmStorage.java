package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long index = 0;

    @Override
    public Film add(Film film) {
        film.setId(++index);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка удаления фильма с id - " + film.getId() + "" +
                    "! Фильм не найден!");
        }
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            films.replace(film.getId(), oldFilm, film);
            return oldFilm;
        } else {
            throw new UserNotFoundException("Ошибка обновления данных о фильме с id - " + film.getId() + "" +
                    "! Фильм не найден!");
        }
    }

    public Film get(long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден!", filmId));
        }
        return film;
    }

    @Override
    public void addLike(long userId, long filmId) {

    }

    @Override
    public void deleteLike(long userId, long filmId) {

    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
