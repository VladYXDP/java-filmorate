package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDtoMapper;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Set;
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
        log.info("Запрос на создания отзыва о фильме" + dto.getFilmId() + " пользователем " + dto.getUserId());
        return reviewDtoMapper.postToDto(reviewService.create(reviewDtoMapper.dtoToPost(dto)));
    }

    @PutMapping("/{id}")
    public void update(@Positive @PathVariable Long id) {
        log.info("Редактирования отзыва " + id);
        reviewService.update(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        log.info("Удаление отзыва " + id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewDto get(@Positive @PathVariable Long id) {
        log.info("Получить отзыв " + id);
        return reviewDtoMapper.postToDto(reviewService.get(id));
    }

    @GetMapping
    public Set<ReviewDto> getAllById(@Positive @RequestParam("filmId") Long filmId,
                                     @RequestParam(value = "count", defaultValue = "10") Long count) {
        log.info("Получить список отзывов фильма " + filmId);
        return reviewService.getAllById(filmId, count)
                .stream()
                .map(reviewDtoMapper::postToDto)
                .collect(Collectors.toSet());
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
