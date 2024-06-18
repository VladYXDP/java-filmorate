package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage {

    private static final String TABLE_NAME = "directors";
    private static final String SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private static final String SELECT_DIRECTOR_BY_FILM_ID = "SELECT * "
                                                       + "FROM DIRECTORS "
                                                       + "inner join films on directors.id = films.director_id "
                                                       + "WHERE films.id = ?";
    private static final String SELECT_DIRECTORS = "SELECT * FROM directors";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        return getDirector(directorId);
    }

    public Director getDirector(long directorId) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_DIRECTOR_BY_ID,
                    (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")),
                    directorId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Режиссер с id {} не найден", directorId);
            throw new NotFoundException("Director not found");
        }
    }

    public Director getDirectorByFilmId(long filmId) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_DIRECTOR_BY_FILM_ID,
                    (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")),
                    filmId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Режиссер фильма с id {} не найден", filmId);
            throw new NotFoundException("Режиссер фильма не найден");
        }
    }

    public List<Director> getDirectors() {
        return jdbcTemplate.query(SELECT_DIRECTORS,
                (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")));
    }

    public Director updateDirector(Director director) {
        jdbcTemplate.update(UPDATE_DIRECTOR, director.getName(), director.getId());
        return getDirector(director.getId());
    }

    public Director deleteDirector(long directorId) {
        Director deletedDirector = getDirector(directorId);
        jdbcTemplate.update(DELETE_DIRECTOR, directorId);
        return deletedDirector;
    }
}
