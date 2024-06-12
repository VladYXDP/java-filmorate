package ru.yandex.practicum.filmorate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"reviewId", "isPositive", "content", "userId", "filmId", "useful"})
public class ReviewDto {
    @Positive
    private Long reviewId;
    @NotNull
    @Size(max = 255)
    private String content;
    @NotNull
    @JsonProperty(value = "isPositive")
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    @PositiveOrZero
    private Long useful = 0L;
}
