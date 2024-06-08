package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;

    public Review create(Review review) {
        return reviewDbStorage.create(review);
    }

    public void update(long id) {
        reviewDbStorage.update();
    }

    public void delete(long id) {
        reviewDbStorage.delete(id);
    }

    public Review get(long id) {
        return reviewDbStorage.get(id);
    }

    public Set<Review> getAllById(long filmId, long count) {
        return reviewDbStorage.getAllById(filmId, count);
    }

    public void like(long id, long userId) {
        reviewDbStorage.like(id, userId);
    }

    public void dislike(long id, long userId) {
        reviewDbStorage.dislike(id, userId);
    }

    public void deleteLike(long id, long userId) {
        reviewDbStorage.deleteLike(id, userId);
    }

    public void deleteDislike(long id, long userId) {
        reviewDbStorage.deleteDislike(id, userId);
    }
}
