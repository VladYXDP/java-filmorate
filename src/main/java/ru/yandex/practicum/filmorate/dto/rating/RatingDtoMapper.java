package ru.yandex.practicum.filmorate.dto.rating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

@Component
public class RatingDtoMapper {

    public RatingDto ratingToDto(Rating rating) {
        if (rating != null) {
            return new RatingDto(rating.getId(), rating.getRating());
        }
        return null;
    }
}
