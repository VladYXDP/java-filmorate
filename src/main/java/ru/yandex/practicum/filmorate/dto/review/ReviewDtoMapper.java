package ru.yandex.practicum.filmorate.dto.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewDtoMapper {

    public Review dtoToReview(ReviewDto dto) {
        Review post = new Review();
        post.setReviewId(dto.getReviewId());
        post.setContent(dto.getContent());
        post.setPositive(dto.getIsPositive());
        post.setFilmId(dto.getFilmId());
        post.setUserId(dto.getUserId());
        post.setUseful(dto.getUseful());
        return post;
    }

    public ReviewDto reviewToDto(Review post) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(post.getReviewId());
        dto.setContent(post.getContent());
        dto.setIsPositive(post.isPositive());
        dto.setFilmId(post.getFilmId());
        dto.setUserId(post.getUserId());
        dto.setUseful(post.getUseful());
        return dto;
    }
}
