package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Long> likes = new HashSet<>();
    private Long likesCount = 0L;
    private Long ratingId;
    private Rating mpa;
    private List<Genre> genres = new ArrayList<>();

    public void addLike(long userId) {
        likes.add(userId);
        likesCount++;
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
        likesCount--;
    }
}
