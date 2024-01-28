package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {

    private final Map<Integer, Film> films = new HashMap<>();
    private static int index = 0;

    public Film addFilm(Film film) {
        if (film != null && !film.getDuration().isNegative()) {
            film.setId(++index);
            return films.put(film.getId(), film);
        } else {
            throw new RuntimeException("Ошибка добавления фильма");
        }
    }

    public Film updateFilm(Film film) {
        if (film != null && films.containsKey(film.getId())) {
            return films.replace(film.getId(), film);
        } else {
            throw new RuntimeException("Ошибка обновления фильма");
        }
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
