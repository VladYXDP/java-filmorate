package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDtoMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewDtoMapper reviewDtoMapper;

    @PostMapping
    public ReviewDto create(@Valid @RequestBody ReviewDto dto) {
        if (dto.getUserId() < 0 || dto.getFilmId() < 0) {
            throw new ValidationException("Неправильный id пользователя " + dto.getUserId());
        }
        log.info("Запрос на создание отзыва о фильме" + dto.getFilmId() + " пользователем " + dto.getUserId());
        return reviewDtoMapper.reviewToDto(reviewService.create(reviewDtoMapper.dtoToReview(dto)));
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody ReviewDto dto) {
        log.info("Редактирования отзыва " + dto.getReviewId());
        Review review = reviewService.update(reviewDtoMapper.dtoToReview(dto));
        return reviewDtoMapper.reviewToDto(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        log.info("Удаление отзыва " + id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewDto get(@Positive @PathVariable Long id) {
        log.info("Получить отзыв " + id);
        return reviewDtoMapper.reviewToDto(reviewService.get(id));
    }

    @GetMapping
    public List<ReviewDto> getAll(@RequestParam(value = "filmId", required = false) Long filmId,
                                  @RequestParam(value = "count", defaultValue = "10", required = false) Long count) {
        log.info("Получить список отзывов фильма " + filmId);
        return reviewService.getAll(filmId, count)
                .stream()
                .map(reviewDtoMapper::reviewToDto)
                .sorted(Comparator.comparing(ReviewDto::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@Positive @PathVariable Long id,
                     @Positive @PathVariable Long userId) {
        log.info("Пользователь " + userId + " ставит лайк отзыву " + id);
        reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@Positive @PathVariable Long id,
                        @Positive @PathVariable Long userId) {
        log.info("Пользователь " + userId + " ставит дизлайк " + id);
        reviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Positive @PathVariable Long id,
                           @Positive @PathVariable Long userId) {
        log.info("Пользователь " + userId + " удаляет лайк " + id);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@Positive @PathVariable Long id,
                              @Positive @PathVariable Long userId) {
        log.info("Пользователь " + userId + " удаляет дизлайк " + id);
        reviewService.deleteDislike(id, userId);
    }
}
