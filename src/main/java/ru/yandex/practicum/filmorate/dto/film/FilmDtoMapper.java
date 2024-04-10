package ru.yandex.practicum.filmorate.dto.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreDtoMapper;
import ru.yandex.practicum.filmorate.dto.rating.RatingDtoMapper;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@RequiredArgsConstructor
public class FilmDtoMapper {

    private final RatingDtoMapper ratingDtoMapper;
    private final GenreDtoMapper genreDtoMapper;

    public Film dtoToFilm(FilmDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setDuration(dto.getDuration());
        film.setReleaseDate(dto.getReleaseDate());
        if (dto.getRating() != null) {
            film.setRating(ratingDtoMapper.dtoToRating(dto.getRating()));
        }
        if (!dto.getGenres().isEmpty()) {
            film.setGenres(genreDtoMapper.dtoToGenre(dto.getGenres()));
        }
        return film;
    }

    public FilmDto filmToDto(Film film) {
        if (film != null) {
            FilmDto dto = new FilmDto();
            dto.setId(film.getId());
            dto.setName(film.getName());
            dto.setDescription(film.getDescription());
            dto.setDuration(film.getDuration());
            dto.setReleaseDate(film.getReleaseDate());
            return dto;
        }
        return null;
    }
}
