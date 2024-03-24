package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDtoMapper;
import ru.yandex.practicum.filmorate.service.RatingService;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;
    private final RatingDtoMapper ratingDtoMapper;

    @GetMapping(path = "/{id}")
    public RatingDto getRating(@PathParam("id") long id) {
        log.info("Запрос на получение рейтинга по id = " + id);
        return ratingDtoMapper.genreToDto(ratingService.getRating(id));
    }

    @GetMapping
    public List<RatingDto> getAllRatings() {
        log.info("Запрос на получение списка рейтингов!");
        return ratingService.getAllRating()
                .stream()
                .map(ratingDtoMapper::genreToDto)
                .collect(Collectors.toList());
    }
}
