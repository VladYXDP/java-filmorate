package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(long id) {
        if (checkGenre(id)) {
            String genreQuery = "SELECT * FROM GENRES WHERE id = ?";
            return jdbcTemplate.queryForObject(genreQuery, this::getGenreMapper, id);
        } else {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден!");
        }
    }

    public List<Genre> getAllGenres() {
        String genresQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(genresQuery, this::getGenreMapper);
    }

    public boolean checkGenre(String name) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM GENRES WHERE name = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, name);
    }

    public boolean checkGenre(long id) {
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM GENRES WHERE id = ?)";
        return jdbcTemplate.queryForObject(checkQuery, Boolean.class, id);
    }

    public boolean checkGenre(List<Genre> genres) {
        return genres.stream().map(Genre::getId).anyMatch(this::checkGenre);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        String getGenresQuery = "SELECT g.id, g.name FROM FILMS_GENRES AS fg INNER JOIN GENRES AS g ON GENRES.id = fg.genres_id " +
                "WHERE fg.films_id = ?";
        return jdbcTemplate.query(getGenresQuery, this::getGenreMapper);
    }

    private Genre getGenreMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }
}
