package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.FilmCreateException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    private static final String INSERT_FILM_WITH_MPA = "INSERT INTO FILMS (name, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)";
    private static final String INSERT_FILM_WITHOUT_MPA = "INSERT INTO FILMS (name, description, release_date, duration) VALUES (?,?,?,?)";
    private static final String INSERT_FILMS_GENRES = "INSERT INTO films_genres (films_id, genres_id) VALUES (?,?)";
    private static final String DELETE_FILM = "DELETE FROM FILMS WHERE id = ?";
    private static final String DELETE_FILMS_GENRES = "DELETE FROM FILMS_GENRES WHERE films_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
    private static final String SELECT_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    private static final String SELECT_FILMS = "SELECT * FROM films";
    private static final String INSERT_LIKE = "INSERT INTO likes (user_id, film_id) VALUES(?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String SELECT_LIKES = "SELECT count(*) FROM likes WHERE film_id = ?";
    private static final String SELECT_EXISTS_FILM = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE id = ?)";
    private static final String SELECT_EXISTS_LIKE = "SELECT EXISTS(SELECT 1 FROM LIKES WHERE user_id = ? AND film_id = ?)";
    private static final String SELECT_EXISTS_FILMS_GENRES = "SELECT EXISTS(SELECT 1 FROM films_genres WHERE films_id = ? AND genres_id = ?)";
    private static final String SELECT_COMMONS = """
            SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID
            FROM films AS f
            JOIN LIKES AS l ON f.ID = l.FILM_ID
            JOIN LIKES AS lf ON l.FILM_ID = lf.FILM_ID
            WHERE l.USER_ID = ? and lf.USER_ID = ?
            """;

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (film.getMpa() != null) {
            if (ratingStorage.checkRating(film.getMpa().getId())) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(INSERT_FILM_WITH_MPA, new String[]{"id"});
                    stmt.setString(1, film.getName());
                    stmt.setString(2, film.getDescription());
                    stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                    stmt.setLong(4, film.getDuration().getSeconds());
                    stmt.setLong(5, film.getMpa().getId());
                    return stmt;
                }, keyHolder);
                Rating mpa = ratingStorage.getRatingById(film.getMpa().getId());
                film.setMpa(mpa);
            } else {
                throw new FilmCreateException("Рейтинг фильма с id " + film.getMpa().getId() + " не найден!");
            }
        } else {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(INSERT_FILM_WITHOUT_MPA, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(4, film.getDuration().getSeconds());
                return stmt;
            }, keyHolder);
        }
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (!film.getGenres().isEmpty()) {
            if (genreStorage.checkGenre(film.getGenres())) {
                film.getGenres().forEach(it -> {
                    if (!checkFilmGenre(film.getId(), it.getId())) {
                        jdbcTemplate.update(connection -> {
                            PreparedStatement stmt = connection.prepareStatement(INSERT_FILMS_GENRES, new String[]{"id"});
                            stmt.setLong(1, film.getId());
                            stmt.setLong(2, it.getId());
                            return stmt;
                        });
                    }
                });
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());
                film.setGenres(genres);
            } else {
                throw new FilmCreateException("Какого-то жанра не существует!");
            }
        }
        return film;
    }

    @Override
    public Film delete(Film film) {
        if (checkFilm(film.getId())) {
            jdbcTemplate.update(DELETE_FILMS_GENRES, film.getId());
            jdbcTemplate.update(DELETE_FILM, film.getId());
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка удаления фильма " + film.getName() + "!");
        }
    }

    @Override
    public Film update(Film film) {
        if (checkFilm(film.getId())) {
            jdbcTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка обновления фильма " + film.getName() + "!");
        }
    }

    @Override
    public Film get(long filmId) {
        if (checkFilm(filmId)) {
            Film film = jdbcTemplate.queryForObject(SELECT_FILM_BY_ID, this::getRowMapperFilm, filmId);
            if (film.getRatingId() != null && film.getRatingId() > 0) {
                Rating rating = ratingStorage.getRatingById(film.getRatingId());
                film.setMpa(rating);
                film.setRatingId(rating.getId());
            } else {
                film.setRatingId(null);
            }
            List<Genre> genres = genreStorage.getGenresByFilmId(filmId);
            film.setLikesCount(getLikes(filmId));
            film.setGenres(genres);
            return film;
        } else {
            throw new FilmNotFoundException("Ошибка получения фильма " + filmId + "!");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query(SELECT_FILMS, this::getRowMapperFilm);
        films.forEach(it -> {
            if (it.getRatingId() > 0) {
                Rating rating = ratingStorage.getRatingById(it.getRatingId());
                it.setRatingId(rating.getId());
                it.setMpa(rating);
            }
            List<Genre> genres = genreStorage.getGenresByFilmId(it.getId());
            it.setGenres(genres);
            it.setLikesCount(getLikes(it.getId()));
        });
        return films;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> films = jdbcTemplate.query(SELECT_COMMONS, this::getRowMapperFilm, userId, friendId);
        films.forEach(film -> {
            if (film.getRatingId() != null) {
                film.setMpa(ratingStorage.getRatingById(film.getRatingId()));
            }
            film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        });
        return films;
    }

    @Override
    public void addLike(long userId, long filmId) {
        if (!checkLike(userId, filmId)) {
            userStorage.get(userId);
            get(filmId);
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(INSERT_LIKE, new String[]{"id"});
                stmt.setLong(1, userId);
                stmt.setLong(2, filmId);
                return stmt;
            });
        }
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        if (checkLike(userId, filmId)) {
            jdbcTemplate.update(DELETE_LIKE, userId, filmId);
        }
    }

    private long getLikes(long filmId) {
        return jdbcTemplate.queryForObject(SELECT_LIKES, Long.class, filmId);
    }

    private boolean checkFilm(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_FILM, Boolean.class, id);
    }

    private boolean checkLike(long userId, long filmId) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_LIKE, Boolean.class, userId, filmId);
    }

    private boolean checkFilmGenre(long filmId, long genreId) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_FILMS_GENRES, Boolean.class, filmId, genreId);
    }

    private Film getRowMapperFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofSeconds(resultSet.getLong("duration")));
        film.setRatingId(resultSet.getLong("rating_id"));
        return film;
    }
}
