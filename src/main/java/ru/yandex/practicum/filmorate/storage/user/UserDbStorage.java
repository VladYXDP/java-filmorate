package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (user != null) {
            String addQuery = "INSERT INTO users (id, email, name, birthday, login) VALUES (?,?,?,?)";
            jdbcTemplate.update(addQuery, user.getEmail(), user.getName(), user.getBirthday(), user.getLogin());
            return user;
        } else {
            throw new UserIsNullException("Ошибка создания пользователя!");
        }
    }

    @Override
    public User delete(User user) {
        String deleteQuery = "DELETE FROM USERS WHERE id = ?";
        jdbcTemplate.update(deleteQuery, user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        String updateQuery = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ?";
        jdbcTemplate.update(updateQuery, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        return user;
    }

    @Override
    public User get(long id) {
        String getQuery = "SELECT id, name, email, login, birthday FROM users WHERE id = ?";
        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet(getQuery);
        return getRowMapperUser(userRowSet);
    }

    @Override
    public Map<Long, User> getAllUser() {
        String getAllQuery = "SELECT * FROM users";
        jdbcTemplate.query(getAllQuery, (rs, rowNum) -> {});
    }

    private User getRowMapperUser(SqlRowSet userRowSet) {
        User user = new User();
        if (userRowSet.next()) {
            user.setId(userRowSet.getLong("id"));
            user.setName(userRowSet.getString("name"));
            user.setLogin(userRowSet.getString("login"));
            user.setBirthday(LocalDate.parse(Objects.requireNonNull(userRowSet.getString("birthday"))));
        }
        return user;
    }

    private User getAllRowMapperUser(User user, ResultSet rs) {
        User user = new User();

    }
}
