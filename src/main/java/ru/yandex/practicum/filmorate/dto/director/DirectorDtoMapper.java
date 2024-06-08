package ru.yandex.practicum.filmorate.dto.director;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public class DirectorDtoMapper {

    public Director toEntity(DirectorDto dto) {
        return new Director(dto.getId(), dto.getName());
    }

    public DirectorDto toDto(Director entity) {
        return new DirectorDto(entity.getId(), entity.getName());
    }

    public List<Director> toEntityList(List<DirectorDto> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<DirectorDto> toDtoList(List<Director> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
