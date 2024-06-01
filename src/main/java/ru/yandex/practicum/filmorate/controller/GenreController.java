package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDtoMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {

    private final GenreService genreService;
    private final GenreDtoMapper genreDtoMapper;

    @GetMapping(path = "/{id}")
    public GenreDto getGenre(@PathVariable long id) {
        log.info("Запрос на получение жанра по id = " + id);
        return genreDtoMapper.genreToDto(genreService.getGenre(id));
    }

    @GetMapping
    public List<GenreDto> getAllUser() {
        log.info("Запрос на получение списка жанров!");
        return genreService.getAllGenre()
                .stream()
                .map(genreDtoMapper::genreToDto)
                .collect(Collectors.toList());
    }
}
