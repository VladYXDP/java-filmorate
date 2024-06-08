package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review post) {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Review get(long id) {
        return null;
    }

    @Override
    public Set<Review> getAllById(long filmId, long count) {
        return null;
    }

    @Override
    public void like(long id, long userId) {

    }

    @Override
    public void dislike(long id, long userId) {

    }

    @Override
    public void deleteLike(long id, long userId) {

    }

    @Override
    public void deleteDislike(long id, long userId) {

    }
}
