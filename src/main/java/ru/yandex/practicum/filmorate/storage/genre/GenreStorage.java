package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_GENRES = "SELECT * FROM GENRES WHERE id = ?";
    private static final String SELECT_ALL_GENRES = "SELECT * FROM GENRES";
    private static final String SELECT_EXISTS_GENRES = "SELECT EXISTS(SELECT 1 FROM GENRES WHERE id = ?)";
    private static final String SELECT_GENRES_BY_FILM = "SELECT g.id, g.name " +
            "FROM FILMS_GENRES AS fg " +
            "INNER JOIN GENRES AS g ON g.id = fg.genres_id " +
            "WHERE fg.films_id = ? " +
            "ORDER BY g.id ASC";

    public Genre getGenreById(long id) {
        List<Genre> genre = jdbcTemplate.query(SELECT_GENRES, this::getGenreMapper, id);
        if (genre.size() != 1) {
            throw new NotFoundException("Жанр с id " + id + " не найден!");
        }
        return jdbcTemplate.queryForObject(SELECT_GENRES, this::getGenreMapper, id);
    }

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SELECT_ALL_GENRES, this::getGenreMapper);
    }

    public boolean checkGenre(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_GENRES, Boolean.class, id);
    }

    public boolean checkGenre(List<Genre> genres) {
        return genres.stream().map(Genre::getId).anyMatch(this::checkGenre);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        return jdbcTemplate.query(SELECT_GENRES_BY_FILM, this::getGenreMapper, filmId);
    }

    private Genre getGenreMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }
}
