package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.review.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventTypeEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    private static final String INSERT_REVIEW = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?,?,?,?)";
    private static final String SELECT_REVIEW = "SELECT * FROM reviews WHERE id = ?";
    private static final String SELECT_ALL_REVIEWS = "SELECT * FROM reviews LIMIT ?";
    private static final String SELECT_EXISTS_REVIEW = "SELECT EXISTS(SELECT 1 FROM reviews WHERE id = ?)";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ?";
    private static final String UPDATE_USEFUL = "UPDATE reviews SET useful = ? WHERE id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = ?";
    private static final String INSERT_LIKE = "INSERT INTO likes_reviews (user_id, review_id) VALUES (?,?)";
    private static final String INSERT_DISLIKE = "INSERT INTO dislikes_reviews (user_id, review_id) VALUES (?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes_reviews WHERE review_id = ? AND user_id = ?";
    private static final String DELETE_DISLIKE = "DELETE FROM dislikes_reviews WHERE review_id = ? AND user_id = ?";
    private static final String SELECT_EXISTS_LIKE = "SELECT EXISTS(SELECT 1 FROM likes_reviews WHERE user_id = ? AND review_id = ?)";
    private static final String SELECT_EXISTS_DISLIKE = "SELECT EXISTS(SELECT 1 FROM dislikes_reviews WHERE user_id = ? AND review_id = ?)";
    private static final String DELETE_LIKES_REVIEW = "DELETE FROM likes_reviews WHERE review_id = ?";
    private static final String DELETE_DISLIKES_REVIEW = "DELETE FROM dislikes_reviews WHERE review_id = ?";
    private static final String SELECT_EXISTS_LIKES_BY_ID = "SELECT EXISTS(SELECT 1 FROM likes_reviews WHERE review_id = ?)";
    private static final String SELECT_EXISTS_DISLIKES_BY_ID = "SELECT EXISTS(SELECT 1 FROM dislikes_reviews WHERE review_id = ?)";
    private static final String SELECT_ALL_REVIEWS_BY_ID = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";


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
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        feedStorage.create(new Feed(review.getUserId(), EventTypeEnum.REVIEW, OperationEnum.ADD, review.getReviewId()));
        return get(review.getReviewId());
    }

    @Override
    public Review update(Review review) {
        get(review.getReviewId());
        jdbcTemplate.update(UPDATE_REVIEW, review.getContent(), review.isPositive());
        Review reviewNew = get(review.getReviewId());
        feedStorage.create(new Feed(reviewNew.getUserId(), EventTypeEnum.REVIEW, OperationEnum.UPDATE, reviewNew.getReviewId()));
        return get(review.getReviewId());
    }

    @Override
    public void updateUseful(Review review) {
        jdbcTemplate.update(UPDATE_USEFUL, review.getUseful(), review.getReviewId());
    }

    @Override
    public void delete(long id) {
        if (checkReview(id)) {
            if (checkLike(id)) {
                jdbcTemplate.update(DELETE_LIKES_REVIEW, id);
            }
            if (checkDislike(id)) {
                jdbcTemplate.update(DELETE_DISLIKES_REVIEW, id);
            }
            Review review = get(id);
            jdbcTemplate.update(DELETE_REVIEW, id);
            feedStorage.create(
                    new Feed(review.getUserId(), EventTypeEnum.REVIEW, OperationEnum.REMOVE, review.getReviewId()));
        } else {
            throw new ReviewNotFoundException("Ошибка удаления отзыва " + id);
        }
    }

    @Override
    public Review get(long id) {
        if (checkReview(id)) {
            return jdbcTemplate.queryForObject(SELECT_REVIEW, this::getRowMapperReview, id);
        } else {
            throw new ReviewNotFoundException("Ошибка получения фильма " + id);
        }
    }

    @Override
    public List<Review> getAll(Long filmId, Long count) {
        List<Review> reviews;
        if (filmId == null) {
            reviews = jdbcTemplate.query(SELECT_ALL_REVIEWS, this::getRowMapperReview, count);
        } else {
            filmStorage.get(filmId);
            reviews = jdbcTemplate.query(SELECT_ALL_REVIEWS_BY_ID, this::getRowMapperReview, filmId, count);
        }
        return reviews;
    }

    @Override
    public void like(long id, long userId) {
        if (checkReview(id)) {
            if (!checkLike(id, userId)) {
                userStorage.get(userId);
                jdbcTemplate.update(con -> {
                    PreparedStatement stmt = con.prepareStatement(INSERT_LIKE, new String[]{"id"});
                    stmt.setLong(1, userId);
                    stmt.setLong(2, id);
                    return stmt;
                });
                if (checkDislike(id, userId)) {
                    deleteDislike(id, userId);
                    changeUseful(id, 1);
                } else {
                    changeUseful(id, 1);
                }
            } else {
                throw new ReviewLikeAlreadyExistsException(
                        "Ошибка добавление лайка к отзыву " + id + " пользователем " + userId);
            }
        } else {
            throw new ReviewNotFoundException("Ошибка добавления лайка к отзыву " + id + " пользователем " + userId);
        }
    }

    @Override
    public void dislike(long id, long userId) {
        if (checkReview(id)) {
            if (!checkDislike(id, userId)) {
                userStorage.get(userId);
                jdbcTemplate.update(con -> {
                    PreparedStatement stmt = con.prepareStatement(INSERT_DISLIKE, new String[]{"id"});
                    stmt.setLong(1, userId);
                    stmt.setLong(2, id);
                    return stmt;
                });
                if (checkLike(id, userId)) {
                    deleteLike(id, userId);
                    changeUseful(id, -1);
                } else {
                    changeUseful(id, -1);
                }
            } else {
                throw new ReviewDislikeAlreadyExistsException(
                        "Ошибка добавление дизлайка для отзыва " + id + " пользователем " + userId);
            }
        } else {
            throw new ReviewNotFoundException("Ошибка добавления дизлайка к отзыву " + id + " пользователем " + userId);
        }
    }

    @Override
    public void deleteLike(long id, long userId) {
        if (checkReview(id)) {
            if (checkLike(id, userId)) {
                userStorage.get(userId);
                jdbcTemplate.update(DELETE_LIKE, id, userId);
                changeUseful(id, -1);
            } else {
                throw new ReviewLikeNotFoundException(
                        "Ошибка удаления лайка у отзыва " + id + " пользователем " + userId);
            }
        } else {
            throw new ReviewNotFoundException("Ошибка удаления лайка к отзыву " + id + " пользователем " + userId);
        }
    }

    @Override
    public void deleteDislike(long id, long userId) {
        if (checkReview(id)) {
            if (checkDislike(id, userId)) {
                userStorage.get(userId);
                jdbcTemplate.update(DELETE_DISLIKE, userId, id);
            } else {
                throw new ReviewDislikeNotFoundException(
                        "Ошибка удаления дизлайка у отзыва " + id + " пользователем " + userId);
            }
        } else {
            throw new ReviewNotFoundException("Ошибка удаления дизлайка к отзыву " + id + " пользователем " + userId);
        }
    }

    private void checkUserAndFilm(long userId, long filmId) {
        userStorage.get(userId);
        filmStorage.get(filmId);
    }

    private boolean checkLike(long id, long userId) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_LIKE, Boolean.class, userId, id);
    }

    private boolean checkLike(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_LIKES_BY_ID, Boolean.class, id);
    }

    private boolean checkDislike(long id, long userId) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_DISLIKE, Boolean.class, userId, id);
    }

    private boolean checkDislike(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_DISLIKES_BY_ID, Boolean.class, id);
    }

    private boolean checkReview(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_REVIEW, Boolean.class, id);
    }

    private void changeUseful(long id, int usefulChange) {
        Review review = get(id);
        review.setUseful(review.getUseful() + usefulChange);
        updateUseful(review);
    }

    private Review getRowMapperReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("id"));
        review.setContent(rs.getString("content"));
        review.setPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUseful(rs.getLong("useful"));
        return review;
    }
}
