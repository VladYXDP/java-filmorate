package ru.yandex.practicum.filmorate.dto.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewDtoMapper {

    public Review dtoToPost(ReviewDto dto) {
        Review post = new Review();
        post.setReviewId(dto.getReviewId());
        post.setContent(dto.getContent());
        post.setPositive(dto.isPositive());
        post.setFilmId(dto.getFilmId());
        post.setUserId(dto.getUserId());
        post.setUseful(dto.getUseful());
        return post;
    }

    public ReviewDto postToDto(Review post) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(post.getReviewId());
        dto.setContent(post.getContent());
        dto.setPositive(post.isPositive());
        dto.setFilmId(post.getFilmId());
        dto.setUserId(post.getUserId());
        dto.setUseful(post.getUseful());
        return dto;
    }
}
