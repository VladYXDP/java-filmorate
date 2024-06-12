package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendDbStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.SlopeOneRecommender;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendDbStorage recommendDbStorage;
    private final SlopeOneRecommender slopeOneRecommender;
    private final FilmDbStorage filmDbStorage;

    public List<Film> recommendFilms(Long userId) {
        Map<Long, Map<Long, Integer>> data = recommendDbStorage.getUserLikes();
        //Строим матрицы различий и частот
        slopeOneRecommender.buildMatrices(data);
        //Получаем лайки конкретного пользователя
        Map<Long, Integer> userRatings = data.get(userId);
        return slopeOneRecommender.recommend(userRatings, filmDbStorage);
    }
}
