package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public Rating getRatingById(long id) {
        String genreQuery = "SELECT * FROM RATINGS WHERE id = ?";
        return jdbcTemplate.queryForObject(genreQuery, this::getRatingMapper, id);
    }

    public List<Rating> getAllRatings() {
        String genresQuery = "SELECT * FROM RATINGS";
        return jdbcTemplate.query(genresQuery, this::getRatingMapper);
    }

    private Rating getRatingMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Rating(
                resultSet.getLong("id"),
                resultSet.getString("rating")
        );
    }
}