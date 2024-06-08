package ru.yandex.practicum.filmorate.dto.director;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class DirectorDto {

    private final int id;
    @NotNull
    @NotBlank
    private final String name;
}
