package ru.yandex.practicum.filmorate.storage.review;

import java.util.Set;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(long id);

    Review get(long id);

    Set<Review> getAllById(long filmId, long count);

    void like(long id, long userId);

    void dislike(long id, long userId);

    void deleteLike(long id, long userId);

    void deleteDislike(long id, long userId);
}
