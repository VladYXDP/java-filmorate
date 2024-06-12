package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Review {
    private Long reviewId;
    private String content;
    private boolean isPositive;
    private Long userId;
    private Long filmId;
    private Long useful;
}
