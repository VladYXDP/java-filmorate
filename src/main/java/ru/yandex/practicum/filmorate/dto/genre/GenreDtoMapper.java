package ru.yandex.practicum.filmorate.dto.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreDtoMapper {

    public Set<Genre> dtoToGenre(Set<GenreDto> dto) {
        return dto.stream().map(this::dtoToGenre).collect(Collectors.toSet());
    }

    public Genre dtoToGenre(GenreDto dto) {
        return new Genre(dto.getId(), dto.getName());
    }

    public Set<GenreDto> genreToDto(Set<Genre> genres) {
        return genres.stream().map(this::genreToDto).collect(Collectors.toSet());
    }

    public GenreDto genreToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
