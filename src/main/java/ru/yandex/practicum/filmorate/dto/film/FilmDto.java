package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.validate.annotation.ReleaseValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class FilmDto {

    private long id;
    @NotBlank
    private String name;
    @Size(max = 200, message = "Описание не должно быть больше 200 символов")
    private String description;
    @NotNull
    @ReleaseValidation
    private LocalDate releaseDate;
    @NotNull
    private Long duration;
    @PositiveOrZero
    private Long likeCount;
    private RatingDto mpa;
    private List<GenreDto> genres = new ArrayList<>();
    private List<DirectorDto> directors = new ArrayList<>();
}
