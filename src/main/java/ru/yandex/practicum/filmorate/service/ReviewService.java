package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void delete(long id) {
        reviewStorage.delete(id);
    }

    public Review get(long id) {
        return reviewStorage.get(id);
    }

    public Set<Review> getAll(Long filmId, Long count) {
        return reviewStorage.getAll(filmId, count);
    }

    public void like(long id, long userId) {
        reviewStorage.like(id, userId);
    }

    public void dislike(long id, long userId) {
        reviewStorage.dislike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        reviewStorage.deleteLike(id, userId);
    }

    public void deleteDislike(long id, long userId) {
        reviewStorage.deleteDislike(id, userId);
    }
}
