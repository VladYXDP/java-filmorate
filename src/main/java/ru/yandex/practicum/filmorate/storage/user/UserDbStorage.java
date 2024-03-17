package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
        return (User) jdbcTemplate.query(getQuery, this::getRowMapperUser).get(0);
    }

    @Override
    public Map<Long, User> getAllUser() {
        String getAllQuery = "SELECT * FROM users";
    }

    private List<User> getRowMapperUser(ResultSet resultSet, int rowNum) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setLogin(resultSet.getString("login"));
            user.setBirthday(LocalDate.parse(Objects.requireNonNull(resultSet.getString("birthday"))));
            users.add(user);
        }
        return users;
    }
}
