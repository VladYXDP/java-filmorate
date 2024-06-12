package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage {

    private static final String TABLE_NAME = "directors";

    private final JdbcTemplate jdbcTemplate;

    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        return getDirector(directorId);
    }

    public Director getDirector(long directorId) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")),
                    directorId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Режиссер с id {} не найден", directorId);
            throw new DirectorNotFoundException("Director not found");
        }
    }

    public Director getDirectorByFilmId(long filmId) {
        String sql = "SELECT * "
                     + "FROM DIRECTORS "
                     + "inner join films on directors.id = films.director_id "
                     + "WHERE films.id = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")),
                    filmId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Режиссер фильма с id {} не найден", filmId);
            throw new DirectorNotFoundException("Режиссер фильма не найден");
        }
    }

    public List<Director> getDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")));
    }

    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return getDirector(director.getId());
    }

    public Director deleteDirector(long directorId) {
        Director deletedDirector = getDirector(directorId);
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, directorId);
        return deletedDirector;
    }
}
