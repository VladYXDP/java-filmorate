package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Set;

public interface ReviewStorage {
    Review create(Review post);
    void update();
    void delete(long id);
    Review get(long id);
    Set<Review> getAllById(long id, long count);
    void like(long id, long userId);
    void dislike(long id, long userId);
    void deleteLike(long id, long userId);
    void deleteDislike(long id, long userId);
}