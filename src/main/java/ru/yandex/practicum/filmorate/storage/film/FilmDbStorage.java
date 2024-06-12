package ru.yandex.practicum.filmorate.storage.film;

import java.sql.Array;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmCreateException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.SortException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Component(value = "filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final DirectorDbStorage directorStorage;
    private final UserStorage userStorage;

    private static final String INSERT_FILM_WITH_MPA = "INSERT INTO FILMS (name, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)";
    private static final String INSERT_FILM_WITH_MPA_AND_DIRECTOR =
            "INSERT INTO FILMS (name, description, release_date, duration, rating_id, director_id) VALUES (?,?,?,?,?,?)";
    private static final String INSERT_FILM_WITHOUT_MPA = "INSERT INTO FILMS (name, description, release_date, duration) VALUES (?,?,?,?)";
    private static final String INSERT_FILM_WITHOUT_MPA_AND_WITH_DIRECTOR = "INSERT INTO FILMS (name, description, release_date, duration, director_id) VALUES (?,?,?,?,?)";
    private static final String INSERT_FILMS_GENRES = "INSERT INTO films_genres (films_id, genres_id) VALUES (?,?)";
    private static final String DELETE_FILM = "DELETE FROM FILMS WHERE id = ?";
    private static final String DELETE_FILMS_GENRES = "DELETE FROM FILMS_GENRES WHERE films_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
    private static final String UPDATE_FILM_WITH_DIRECTOR = "UPDATE FILMS SET name = ?, description = ?, release_date = ?, duration = ?, director_id = ? WHERE id = ?";
    private static final String SELECT_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    private static final String SELECT_FILMS = "SELECT * FROM films";
    private static final String INSERT_LIKE = "INSERT INTO likes (user_id, film_id) VALUES(?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String SELECT_LIKES = "SELECT count(*) FROM likes WHERE film_id = ?";
    private static final String SELECT_EXISTS_FILM = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE id = ?)";
    private static final String SELECT_EXISTS_LIKE = "SELECT EXISTS(SELECT 1 FROM LIKES WHERE user_id = ? AND film_id = ?)";
    private static final String SELECT_EXISTS_FILMS_GENRES = "SELECT EXISTS(SELECT 1 FROM films_genres WHERE films_id = ? AND genres_id = ?)";

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        boolean withoutDirector = film.getDirectors().isEmpty();
        if (film.getMpa() != null) {
            if (ratingStorage.checkRating(film.getMpa().getId())) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(
                            withoutDirector ? INSERT_FILM_WITH_MPA : INSERT_FILM_WITH_MPA_AND_DIRECTOR,
                            new String[]{"id"});
                    stmt.setString(1, film.getName());
                    stmt.setString(2, film.getDescription());
                    stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                    stmt.setLong(4, film.getDuration());
                    stmt.setLong(5, film.getMpa().getId());
                    if (!withoutDirector) {
                        stmt.setLong(6, film.getDirectors().get(0).getId());
                    }
                    return stmt;
                }, keyHolder);
                Rating mpa = ratingStorage.getRatingById(film.getMpa().getId());
                film.setMpa(mpa);
            } else {
                throw new FilmCreateException("Рейтинг фильма с id " + film.getMpa().getId() + " не найден!");
            }
        } else {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(
                        withoutDirector ? INSERT_FILM_WITHOUT_MPA : INSERT_FILM_WITHOUT_MPA_AND_WITH_DIRECTOR,
                        new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(4, film.getDuration());
                if (!withoutDirector) {
                    stmt.setLong(5, film.getDirectors().get(0).getId());
                }
                return stmt;
            }, keyHolder);
        }
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (!film.getGenres().isEmpty()) {
            if (genreStorage.checkGenre(film.getGenres())) {
                film.getGenres().forEach(it -> {
                    if (!checkFilmGenre(film.getId(), it.getId())) {
                        jdbcTemplate.update(connection -> {
                            PreparedStatement stmt = connection.prepareStatement(INSERT_FILMS_GENRES,
                                    new String[]{"id"});
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
        return get(film.getId());
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
            if (!film.getDirectors().isEmpty()) {
                return updateFilmWithDirector(film);
            }
            jdbcTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getId());
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
            try {
                film.setDirectors(List.of(directorStorage.getDirectorByFilmId(filmId)));
            } catch (DirectorNotFoundException e) {
                film.setDirectors(List.of());
            }
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

    @Override
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        String sql = "SELECT f.id as film_id, "
                + "f.name as name, "
                + "f.description as description, "
                + "f.release_date as release_date, "
                + "f.duration as duration, "
                + "d.id AS director_id, "
                + "d.name AS director_name, "
                + "r.id AS rating_id, "
                + "r.name AS rating_name, "
                + "array_agg(l.USER_ID) AS likes "
                + "FROM films f "
                + "JOIN directors d ON d.id = f.director_id "
                + "JOIN ratings r ON r.id = f.rating_id "
                + "LEFT JOIN likes l ON l.film_id = f.id "
                + "WHERE f.director_id = ? "
                + "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, d.id, d.name, r.id, r.name ";
        switch (sortBy) {
            case "year": {
                sql = sql + "ORDER BY extract(year from f.release_date);";
                return getListFilmWithDirector(sql, directorId);
            }
            case "likes": {
                sql = sql + "ORDER BY count(l.USER_ID) DESC;";
                return getListFilmWithDirector(sql, directorId);
            }
            default:
                throw new SortException();
        }
    }

    private List<Film> getListFilmWithDirector(String sql, long directorId) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getLong("duration"))
                    .build();

            Optional.of(film)
                    .ifPresent(f -> {
                        try {
                            Rating mpa = new Rating(rs.getLong("rating_id"),
                                    rs.getString("rating_name"));

                            Director director = new Director(rs.getInt("director_id"),
                                    rs.getString("director_name"));

                            Array likesArray = rs.getArray("likes");
                            Set<Long> likes = new HashSet<>();
                            if (likesArray != null) {
                                ResultSet likesResultSet = likesArray.getResultSet();
                                while (likesResultSet.next()) {
                                    likes.add(likesResultSet.getLong("VALUE"));
                                }
                            }
                            f.setMpa(mpa);
                            f.setGenres(genreStorage.getGenresByFilmId(f.getId()));
                            f.setLikes(likes);
                            f.getDirectors().add(director);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return film;

        }, directorId);
    }

    private Film updateFilmWithDirector(Film film) {
        jdbcTemplate.update(UPDATE_FILM_WITH_DIRECTOR, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getDirectors().get(0).getId(),
                film.getId());

        return get(film.getId());
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
        film.setDuration(resultSet.getLong("duration"));
        film.setRatingId(resultSet.getLong("rating_id"));

        return film;
    }
}
