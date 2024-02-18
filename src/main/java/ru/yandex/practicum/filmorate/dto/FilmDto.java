package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.formatter.DurationDeserializeFormatter;
import ru.yandex.practicum.filmorate.formatter.DurationSerializeFormatter;
import ru.yandex.practicum.filmorate.validate.annotation.ReleaseValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

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
    @Positive
    @JsonSerialize(using = DurationSerializeFormatter.class)
    @JsonDeserialize(using = DurationDeserializeFormatter.class)
    private Duration duration;
}
