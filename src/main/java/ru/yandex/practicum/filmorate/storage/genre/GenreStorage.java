package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(long id) {
        String genreQuery = "SELECT * FROM GENRES WHERE id = ?";
        return jdbcTemplate.queryForObject(genreQuery, this::getGenreMapper, id);
    }

    public List<Genre> getAllGenres() {
        String genresQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(genresQuery, this::getGenreMapper);
    }

    private Genre getGenreMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getLong("id"),
                resultSet.getString("genre")
        );
    }
}
