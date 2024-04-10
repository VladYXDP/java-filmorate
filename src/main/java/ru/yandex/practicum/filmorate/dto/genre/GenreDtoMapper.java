package ru.yandex.practicum.filmorate.dto.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreDtoMapper {

    public List<Genre> dtoToGenre(List<GenreDto> dto) {
        return dto.stream().map(this::dtoToGenre).collect(Collectors.toList());
    }

    public Genre dtoToGenre(GenreDto dto) {
        return new Genre(dto.getId(), dto.getName());
    }

    public GenreDto genreToDto(Genre genre) {
        if (genre != null) {
            return new GenreDto(genre.getId(), genre.getName());
        }
        return null;
    }
}
