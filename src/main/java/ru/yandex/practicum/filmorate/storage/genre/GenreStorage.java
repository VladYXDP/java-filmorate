package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(long id) {
        String genreQuery = "SELECT * FROM GENRES WHERE id = ?";
        return jdbcTemplate.queryForObject(genreQuery, this::getGenreMapper);
    }

    public List<Genre> getAllGenres() {
        String genresQuery = "SELCT * FROM GENRES";
        return jdbcTemplate.queryForObject(genresQuery, this::getAllGenresMapper);
    }

    private Genre getGenreMapper(ResultSet resultSet, int rowNum) throws SQLException {
        if (resultSet.next()) {
            return new Genre(
                    resultSet.getLong("id"),
                    resultSet.getString("genre")
            );
        }
        return null;
    }

    private List<Genre> getAllGenresMapper(ResultSet resultSet, int rowNum) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        while (resultSet.next()) {
            genres.add(new Genre(
                    resultSet.getLong("id"),
                    resultSet.getString("genre")
            ));
        }
        return genres;
    }
}
