package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public Rating getRatingById(long id) {
        String genreQuery = "SELECT * FROM RATING WHERE id = ?";
        return jdbcTemplate.queryForObject(genreQuery, this::getRatingMapper);
    }

    public List<Rating> getAllRatings() {
        String genresQuery = "SELCT * FROM GENRES";
        return jdbcTemplate.queryForObject(genresQuery, this::getAllRatingsMapper);
    }

    private Rating getRatingMapper(ResultSet resultSet, int rowNum) throws SQLException {
        if (resultSet.next()) {
            return new Rating(
                    resultSet.getLong("id"),
                    resultSet.getString("rating")
            );
        }
        return null;
    }

    private List<Rating> getAllRatingsMapper(ResultSet resultSet, int rowNum) throws SQLException {
        List<Rating> ratings = new ArrayList<>();
        while (resultSet.next()) {
            ratings.add(new Rating(
                    resultSet.getLong("id"),
                    resultSet.getString("rating")
            ));
        }
        return ratings;
    }
}
