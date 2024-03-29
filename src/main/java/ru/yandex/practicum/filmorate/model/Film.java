package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private Set<Long> likes = new HashSet<>();
    private Long likesCount = 0L;

    public void addLike(long userId) {
        likes.add(userId);
        likesCount++;
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
        likesCount--;
    }
}
