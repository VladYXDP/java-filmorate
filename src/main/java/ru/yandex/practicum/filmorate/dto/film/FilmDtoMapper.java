package ru.yandex.practicum.filmorate.dto.film;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDtoMapper;
import ru.yandex.practicum.filmorate.dto.rating.RatingDtoMapper;
import ru.yandex.practicum.filmorate.model.Director;
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
        if (dto.getMpa() != null) {
            film.setMpa(ratingDtoMapper.dtoToRating(dto.getMpa()));
        }
        if (!dto.getGenres().isEmpty()) {
            film.setGenres(genreDtoMapper.dtoToGenre(dto.getGenres()));
        }
        if (!dto.getDirectors().isEmpty()) {
            film.getDirectors().addAll(dto.getDirectors().stream()
                    .map(directorDto -> new Director(directorDto.getId(), directorDto.getName())).collect(
                            Collectors.toList()));
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
            if (film.getMpa() != null) {
                dto.setMpa(ratingDtoMapper.ratingToDto(film.getMpa()));
            }
            if (!film.getGenres().isEmpty()) {
                dto.setGenres(genreDtoMapper.genreToDto(film.getGenres()));
            }
            if (film.getLikesCount() != null) {
                dto.setLikeCount(film.getLikesCount());
            }
            if (!film.getDirectors().isEmpty()) {
                dto.getDirectors().addAll(film.getDirectors().stream()
                        .map(director -> new DirectorDto(director.getId(), director.getName())).collect(
                                Collectors.toList()));
            }
            return dto;
        }
        return null;
    }
}
