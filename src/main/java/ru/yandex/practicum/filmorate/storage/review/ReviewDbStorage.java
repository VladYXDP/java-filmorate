package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private static final String INSERT_REVIEW = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?,?,?,?,?)";
    private static final String SELECT_REVIEW = "SELECT * FROM reviews WHERE id = ?";
    private static final String SELECT_EXISTS_REVIEW = "SELECT EXISTS(SELECT 1 FROM REVIEW WHERE id = ?)";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ?, useful = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = ?";

    @Override
    public Review create(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        checkUserAndFilm(review.getUserId(), review.getFilmId());
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_REVIEW, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.isPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setLong(5, review.getUseful());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        if (checkReview(review.getReviewId())) {
            jdbcTemplate.update(UPDATE_REVIEW, review.getContent(), review.isPositive(), review.getUseful());
            return review;
        } else {
            throw new ReviewNotFoundException("Ошибка обновления отзыва " + review.getReviewId());
        }
    }

    @Override
    public void delete(long id) {
        if (checkReview(id)) {
            jdbcTemplate.update(DELETE_REVIEW, id);
            // TODO: 08.06.2024 удаление из таблицы лайков
        } else {
            throw new ReviewNotFoundException("Ошибка удаления отзыва " + id);
        }
    }

    @Override
    public Review get(long id) {
        if (checkReview(id)) {
            jdbcTemplate.update(SELECT_REVIEW);
        } else {
            throw new ReviewNotFoundException("Ошибка получения фильма " + id);
        }
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

    private void checkUserAndFilm(long userId, long filmId) {
        userStorage.get(userId);
        filmStorage.get(filmId);
    }

    private boolean checkReview(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_REVIEW, Boolean.class, id);
    }
}
