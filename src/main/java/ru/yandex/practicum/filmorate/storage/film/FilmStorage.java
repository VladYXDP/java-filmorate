package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {

    Film add(Film film);

    Film delete(Film film);

    Film update(Film film);

    Map<Long, Film> getAllFilms();
}
