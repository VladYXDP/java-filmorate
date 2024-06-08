package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto create(@Valid @RequestBody ReviewDto dto) {
        log.info("Запрос на создание отзыва о фильме");
    }

    @PutMapping("/{id}")
    public void update(@Positive @PathVariable Long id) {

    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {

    }

    @GetMapping("/{id}")
    public ReviewDto get(@Positive @PathVariable Long id) {

    }

    @GetMapping
    public List<ReviewDto> getAllById(@Positive @RequestParam("filmId") Long filmId,
                                           @RequestParam(value = "count", defaultValue = "10") Long count) {

    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@Positive @PathVariable Long id,
                     @Positive @PathVariable Long userId) {

    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@Positive @PathVariable Long id,
                        @Positive @PathVariable Long userId) {

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Positive @PathVariable Long id,
                           @Positive @PathVariable Long userId) {

    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@Positive @PathVariable Long id,
                              @Positive @PathVariable Long userId) {

    }
}
