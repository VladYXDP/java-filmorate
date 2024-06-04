package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingStorage ratingStorage;

    public Rating getRating(long id) {
        return ratingStorage.getRatingById(id);
    }

    public List<Rating> getAllRating() {
        return ratingStorage.getAllRatings();
    }
}
