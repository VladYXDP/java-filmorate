package ru.yandex.practicum.filmorate.dto.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreDtoMapper {

    public Genre dtoToGenre(GenreDto genreDto) {
        return new Genre(genreDto.getId(), genreDto.getGenre());
    }

    public GenreDto genreToDto(Genre genre) {
        if (genre != null) {
            return new GenreDto(genre.getId(), genre.getGenre());
        }
        return null;
    }
}
