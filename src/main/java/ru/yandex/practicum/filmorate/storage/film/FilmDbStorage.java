package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.rating.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            if (film.getMpa() != null) {
                if (ratingStorage.checkRating(film.getMpa().getId())) {
                    addQuery = "INSERT INTO FILMS (name, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)";
                    jdbcTemplate.update(connection -> {
                        PreparedStatement stmt = connection.prepareStatement(addQuery, new String[]{"id"});
                        stmt.setString(1, film.getName());
                        stmt.setString(2, film.getDescription());
                        stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                        stmt.setLong(4, film.getDuration().getSeconds());
                        stmt.setLong(5, film.getMpa().getId());
                        return stmt;
                    }, keyHolder);
                }
                throw new RatingNotFoundException("Рейтинг фильма с id " + film.getMpa().getId() + " не найден!");
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
            if (!film.getGenres().isEmpty() && genreStorage.checkGenre(film.getGenres())) {
                String queryFilmsGenres = "INSERT INTO films_genres (films_id, genres_id) VALUES (?,?)";
                film.getGenres().forEach(it -> {
                    if (!checkFilmGenre(film.getId(), it.getId())) {
                        jdbcTemplate.update(connection -> {
                            PreparedStatement stmt = connection.prepareStatement(queryFilmsGenres, new String[]{"id"});
                            stmt.setLong(1, film.getId());
                            stmt.setLong(2, it.getId());
                            return stmt;
                        });
                    }
                });
            }
            return film;
        } else {
            throw new FilmAlreadyExistsException("Фильм с названием " + film.getName() + " уже существует!");
        }
    }

    @Override
    public Film delete(Film film) {
        if (checkFilm(film.getId())) {
            String deleteQuery = "DELETE FROM FILMS WHERE id = ?";
            String deleteFilmGenre = "DELETE FROM FILMS_GENRES WHERE film_id = ?";
            jdbcTemplate.update(deleteFilmGenre, film.getId());
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
            Film film = jdbcTemplate.queryForObject(getQuery, this::getRowMapperFilm);
            Rating rating = ratingStorage.getRatingById(film.getRatingId());
            Set<Genre> genres = new HashSet<>(genreStorage.getGenresByFilmId(filmId));
            film.setMpa(rating);
            film.setGenres(genres);
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка получения фильма " + filmId + "!");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String getAllQuery = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(getAllQuery, this::getRowMapperFilm);
        films.forEach(it -> {
            Rating rating = ratingStorage.getRatingById(it.getRatingId());
            it.setMpa(rating);
        });
        films.forEach(it -> {
            Set<Genre> genres = new HashSet<>(genreStorage.getGenresByFilmId(it.getId()));
            it.setGenres(genres);
        });
        return films;
    }

    private boolean checkFilm(Film film) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE name = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, film.getName());
    }

    private boolean checkFilm(long id) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE id = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, id);
    }

    private boolean checkFilmGenre(long filmId, long genreId) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM films_genres WHERE films_id = ? AND genres_id = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, filmId, genreId);
    }

    private Film getRowMapperFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofSeconds(resultSet.getLong("duration")));
        film.setRatingId(resultSet.getLong("rating_id"));
        return film;
    }
}
