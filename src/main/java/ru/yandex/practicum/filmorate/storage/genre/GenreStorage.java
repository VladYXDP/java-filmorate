package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(long id) {
        String genreQuery = "SELECT * FROM GENRES WHERE id = ?";
        List<Genre> genre = jdbcTemplate.query(genreQuery, this::getGenreMapper, id);
        if (genre.size() != 1) {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден!");
        }
        return jdbcTemplate.queryForObject(genreQuery, this::getGenreMapper, id);
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

    public boolean checkGenre(Set<Genre> genres) {
        return genres.stream().map(Genre::getId).anyMatch(this::checkGenre);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        String getGenresQuery = "SELECT g.id, g.name FROM FILMS_GENRES AS fg INNER JOIN GENRES AS g ON g.id = fg.genres_id " +
                "WHERE fg.films_id = ?";
        List<Genre> genres = jdbcTemplate.query(getGenresQuery, this::getGenreMapper, filmId);
        return genres;
    }

    private Genre getGenreMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }
}
