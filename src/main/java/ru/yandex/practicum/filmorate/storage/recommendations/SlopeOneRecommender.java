package ru.yandex.practicum.filmorate.storage.recommendations;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SlopeOneRecommender {
    //Матрица различий, которая хранит различия между оценками фильмов
    private final Map<Long, Map<Long, Double>> differencesMatrix = new HashMap<>();
    //Матрица частот, которая хранит количество совместных лайков для пар фильмов
    private final Map<Long, Map<Long, Integer>> frequenciesMatrix = new HashMap<>();

    public void buildMatrices(Map<Long, Map<Long, Integer>> data) {
        //Проходим по каждому пользователю и его лайкам
        for (Map<Long, Integer> user : data.values()) {
            //Проходим по каждому фильму, который пользователь лайкнул
            for (Map.Entry<Long, Integer> e : user.entrySet()) {
                //Проверяем существует ли элемент в матрицах. Если нет - создаём новую запись
                differencesMatrix.putIfAbsent(e.getKey(), new HashMap<>());
                frequenciesMatrix.putIfAbsent(e.getKey(), new HashMap<>());
                //Обновляем различия и частоты
                for (Map.Entry<Long, Integer> e2 : user.entrySet()) {
                    //Получаем старое значение частоты
                    int oldCount = frequenciesMatrix.get(e.getKey()).getOrDefault(e2.getKey(), 0);
                    //Получаем старое значение различия
                    double oldDiff = differencesMatrix.get(e.getKey()).getOrDefault(e2.getKey(), 0.0);
                    //Вычисляем различие между лайками
                    double observedDiff = e.getValue() - e2.getValue();
                    //Обновляем частоту для пары фильмов
                    frequenciesMatrix.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    //Обновляем различие для пары фильмов
                    differencesMatrix.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        //Нормализуем различия после прохода по всем пользователям
        for (Long j : differencesMatrix.keySet()) {
            //Нормализуем различия для каждой пары фильмов
            for (Long i : differencesMatrix.get(j).keySet()) {
                //Получаем старое значение различия
                double oldValue = differencesMatrix.get(j).get(i);
                //Получаем количество совместных лайков для пары фильмов
                int count = frequenciesMatrix.get(j).get(i);
                //Обновляем значение различия, разделив его на количество совместных лайков
                differencesMatrix.get(j).put(i, oldValue / count);
            }
        }
    }

    //Прогнозирование
    public Map<Long, Double> predict(Map<Long, Integer> userRatings) {
        //Карта прогнозируемых оценок
        Map<Long, Double> predictions = new HashMap<>();
        //Карта частот для прогнозируемых оценок
        Map<Long, Integer> frequencies = new HashMap<>();
        //Инициализируем карты
        for (Long j : differencesMatrix.keySet()) {
            frequencies.put(j, 0);//ставим начальную частоту
            predictions.put(j, 0.0);//ставим начальную прогнозируемую оценку
        }
        //Проходим по оценкам текущего пользователя
        for (Map.Entry<Long, Integer> userRating : userRatings.entrySet()) {
            //Проходим по всем фильмам в матрице различий
            for (Long j : differencesMatrix.keySet()) {
                //Вычисляем новое значение прогнозируемой оценки
                double newVal = (differencesMatrix.get(j).get(userRating.getKey()) + userRating.getValue())
                                * frequenciesMatrix.get(j).get(userRating.getKey());
                //Обновляем значение прогнозируемой оценки для фильма j
                predictions.put(j, predictions.get(j) + newVal);
                //Обновляем значение частоты для фильма j
                frequencies.put(j, frequencies.get(j) + frequenciesMatrix.get(j).get(userRating.getKey()));

            }
        }

        Map<Long, Double> cleanPredictions = new HashMap<>();
        //Нормализуем прогнозируемые оценки
        for (Long j : predictions.keySet()) {
            if (frequencies.get(j) > 0) {
                //Обновляем значение прогнозируемой оценки, разделив его на частоту
                cleanPredictions.put(j, predictions.get(j) / frequencies.get(j));
            }
        }
        //Возвращаем карту нормализованных прогнозируемых оценок
        return cleanPredictions;
    }

    public List<Film> recommend(Map<Long, Integer> userRatings, FilmDbStorage filmDbStorage) {
        //Если у пользователя нет оценок, возвращаем пустой список
        if (userRatings == null || userRatings.isEmpty()) {
            return new ArrayList<>();
        }
        //Вычисляем прогнозируемые оценки для всех фильмов
        Map<Long, Double> predictions = predict(userRatings);
        //Создаем список прогнозируемых оценок
        List<Map.Entry<Long, Double>> recommendations = new ArrayList<>(predictions.entrySet());

        List<Film> recommendedFilms = new ArrayList<>();
        //Проходим по всем прогнозируемым оценкам
        for (Map.Entry<Long, Double> entry : recommendations) {
            //Проверяем оценил пользователь данный фильм или нет
            if (!userRatings.containsKey(entry.getKey())) {
                recommendedFilms.add(filmDbStorage.get(entry.getKey()));
            }
        }

        return new ArrayList<>(recommendedFilms);
    }
}
