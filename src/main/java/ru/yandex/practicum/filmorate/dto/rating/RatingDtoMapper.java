package ru.yandex.practicum.filmorate.dto.rating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RatingDtoMapper {

    public List<Rating> dtoToRating(List<RatingDto> dto) {
        return dto.stream().map(this::dtoToRating).collect(Collectors.toList());
    }

    public Rating dtoToRating(RatingDto dto) {
        return new Rating(dto.getId(), dto.getName());
    }

    public RatingDto ratingToDto(Rating rating) {
        if (rating != null) {
            return new RatingDto(rating.getId(), rating.getName());
        }
        return null;
    }
}
