package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    @Builder.Default
    private Set<Long> likes = new HashSet<>();
    private Long likesCount;
    private Long ratingId;
    private Rating mpa;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
    @Builder.Default
    private List<Director> directors = new ArrayList<>();

    public void addLike(long userId) {
        likes.add(userId);
        likesCount++;
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
        likesCount--;
    }
}
