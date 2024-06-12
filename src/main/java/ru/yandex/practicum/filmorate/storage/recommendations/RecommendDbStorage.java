package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecommendDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Map<Long, Map<Long, Integer>> getUserLikes() {
        final String sqlQuery = "SELECT user_id, film_id FROM likes";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
        Map<Long, Map<Long, Integer>> userLikes = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long userId = ((Number) row.get("user_id")).longValue();
            Long filmId = ((Number) row.get("film_id")).longValue();
            //Если карта для данного пользователя не существует, создаём пустую карту
            userLikes.putIfAbsent(userId, new HashMap<>());
            userLikes.get(userId).put(filmId, 1);//ставим 1 когда пользователь ставил лайк
        }

        return userLikes;
    }
}
