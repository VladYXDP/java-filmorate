package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.rating.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_MPA = "SELECT * FROM RATINGS WHERE id = ?";
    private static final String SELECT_ALL_MPA = "SELECT * FROM RATINGS";
    private static final String SELECT_EXITSTS_MPA = "SELECT EXISTS (SELECT 1 FROM RATINGS WHERE id = ?)";

    public Rating getRatingById(long id) {
        if (checkRating(id)) {
            return jdbcTemplate.queryForObject(SELECT_MPA, this::getRatingMapper, id);
        } else {
            throw new RatingNotFoundException("Рейтинг с id " + id + " не найден!");
        }
    }

    public List<Rating> getAllRatings() {
        return jdbcTemplate.query(SELECT_ALL_MPA, this::getRatingMapper);
    }

    public boolean checkRating(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXITSTS_MPA, Boolean.class, id);
    }

    private Rating getRatingMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Rating(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }
}
