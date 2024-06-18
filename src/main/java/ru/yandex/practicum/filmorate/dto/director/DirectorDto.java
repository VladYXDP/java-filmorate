package ru.yandex.practicum.filmorate.dto.director;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class DirectorDto {

    private final int id;
    @NotBlank(message = "Имя не должно быть пустым")
    private final String name;
}
