package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component(value = "filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Override
    public Film add(Film film) {
        if (!checkFilm(film)) {
            String addQuery;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            if (!film.getGenres().isEmpty() && !checkFilmGenre(film.getId())) {
                String queryFilmsGenres = "INSERT INTO films_genres "
            }
            if (film.getRating() != null) {
                if (ratingStorage.checkRating(film.getRating().getId())) {
                    addQuery = "INSERT INTO FILMS (name, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)";
                    jdbcTemplate.update(connection -> {
                        PreparedStatement stmt = connection.prepareStatement(addQuery, new String[]{"id"});
                        stmt.setString(1, film.getName());
                        stmt.setString(2, film.getDescription());
                        stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                        stmt.setLong(4, film.getDuration().getSeconds());
                        stmt.setLong(5, film.getRating().getId());
                        return stmt;
                    }, keyHolder);
                }
            } else {
                addQuery = "INSERT INTO FILMS (name, description, release_date, duration) VALUES (?,?,?,?)";
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(addQuery, new String[]{"id"});
                    stmt.setString(1, film.getName());
                    stmt.setString(2, film.getDescription());
                    stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                    stmt.setLong(4, film.getDuration().getSeconds());
                    return stmt;
                }, keyHolder);
            }
            film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return film;
        } else {
            throw new FilmAlreadyExistsException("Фильм с названием " + film.getName() + " уже существует!");
        }
    }

    @Override
    public Film delete(Film film) {
        if (checkFilm(film.getId())) {
            String deleteQuery = "DELETE FROM FILMS WHERE id = ?";
            jdbcTemplate.update(deleteQuery, film.getId());
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка удаления фильма " + film.getName() + "!");
        }
    }

    @Override
    public Film update(Film film) {
        if (checkFilm(film.getId())) {
            String updateQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?";
            jdbcTemplate.update(updateQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка обновления фильма " + film.getName() + "!");
        }
    }

    @Override
    public Film get(long filmId) {
        if (checkFilm(filmId)) {
            String getQuery = "SELECT * FROM films WHERE id = ?";
            return jdbcTemplate.queryForObject(getQuery, this::getRowMapperFilm);
        } else {
            throw new FilmNotFoundException("Ошибка получения фильма " + filmId + "!");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String getAllQuery = "SELECT * FROM films";
        return jdbcTemplate.query(getAllQuery, this::getRowMapperFilm);
    }

    private boolean checkFilm(Film film) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE name = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, film.getName());
    }

    private boolean checkFilm(long id) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE id = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, id);
    }

    private boolean checkFilmGenre(long filmId) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM films_genres WHERE films_id = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, filmId);
    }

    private Film getRowMapperFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofSeconds(resultSet.getLong("duration")));
        return film;
    }
}
