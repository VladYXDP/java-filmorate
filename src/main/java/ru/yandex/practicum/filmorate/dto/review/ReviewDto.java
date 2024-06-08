package ru.yandex.practicum.filmorate.dto.review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    @Positive
    private Long reviewId;
    @NotNull
    @Size(max = 255)
    private String content;
    private boolean isPositive;
    @NotNull
    @Positive
    private Long userId;
    @NotNull
    @Positive
    private Long filmId;
    @NotNull
    @Positive
    private Long useful;
}
