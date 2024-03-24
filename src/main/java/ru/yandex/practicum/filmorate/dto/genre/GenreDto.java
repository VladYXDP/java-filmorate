package ru.yandex.practicum.filmorate.dto.genre;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreDto {
    private long id;
    private String genre;
}
