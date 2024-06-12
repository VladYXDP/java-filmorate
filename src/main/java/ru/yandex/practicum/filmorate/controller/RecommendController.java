package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendationService recommendService;

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable long id) {
        return recommendService.recommendFilms(id);
    }
}
