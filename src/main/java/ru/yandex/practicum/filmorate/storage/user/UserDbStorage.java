package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (user != null) {
            String addQuery = "INSERT INTO users (email, name, birthday, login) VALUES (?,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(addQuery, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getName());
                stmt.setDate(3, Date.valueOf(user.getBirthday()));
                stmt.setString(4, user.getLogin());
                return stmt;
            }, keyHolder);
            return user;
        } else {
            throw new UserIsNullException("Ошибка создания пользователя!");
        }
    }

    @Override
    public User delete(User user) {
        String deleteQuery = "DELETE FROM users WHERE id = ?";
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
        return jdbcTemplate.queryForObject(getQuery, this::getRowMapperUser);
    }

    @Override
    public Map<Long, User> getAllUser() {
        String getAllQuery = "SELECT * FROM users";
        return jdbcTemplate.queryForObject(getAllQuery, this::getRowMapperAllUser);
    }

    private User getRowMapperUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = null;
        if (resultSet.next()) {
            user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setLogin(resultSet.getString("login"));
            user.setBirthday(LocalDate.parse(Objects.requireNonNull(resultSet.getString("birthday"))));
        }
        return user;
    }

    private Map<Long, User> getRowMapperAllUser(ResultSet resultSet, int rowNum) throws SQLException {
        Map<Long, User> users = new HashMap<>();
        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setLogin(resultSet.getString("login"));
            user.setBirthday(LocalDate.parse(Objects.requireNonNull(resultSet.getString("birthday"))));
            users.put(user.getId(), user);
        }
        return users;
    }
}
