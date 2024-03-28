package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (!checkUser(user)) {
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
            user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return user;
        }
        throw new UserAlreadyExistException("Пользователь с login " + user.getLogin() + " или email "
                + user.getEmail() + " уже существует!");
    }

    @Override
    public User delete(User user) {
        if (checkUserById(user.getId())) {
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            jdbcTemplate.update(deleteQuery, user.getId());
            return user;
        }
        throw new UserNotFoundException("Ошибка удаления пользователя с id " + user.getId());
    }

    @Override
    public User update(User user) {
        if (checkUserById(user.getId())) {
            String updateQuery = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ?";
            jdbcTemplate.update(updateQuery, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
            return user;
        }
        throw new UserNotFoundException("Ошибка обновления пользователя с id " + user.getId());
    }

    @Override
    public User get(long id) {
        String getQuery = "SELECT u.id, u.name, u.email, u.login, u.birthday, f.friend_id " +
                "FROM users INNER JOIN friends ON user_id = ?";
        return jdbcTemplate.queryForObject(getQuery, this::getRowMapperUser, id);
    }

    @Override
    public List<User> getAllUser() {
        String getAllQuery = "SELECT * FROM users";
        return jdbcTemplate.query(getAllQuery, this::getRowMapperUser);
    }

    public void addFriend(long userId, long friendId) {
        if (checkFriends(userId, friendId)) {
            String insertAddFriendQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?,?,?)";
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(insertAddFriendQuery);
                stmt.setLong(1, userId);
                stmt.setLong(2, friendId);
                stmt.setBoolean(3, true);
                return stmt;
            });
        }
    }

    private Boolean checkUserById(long id) {
        String existsUserByIdQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE id = ?)";
        return jdbcTemplate.queryForObject(existsUserByIdQuery, Boolean.class, id);
    }

    private Boolean checkUser(User user) {
        String existsUserQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE EMAIL = ? OR LOGIN = ?)";
        return jdbcTemplate.queryForObject(existsUserQuery, Boolean.class, user.getEmail(), user.getLogin());
    }

    private Boolean checkFriends(long userId, long friendId) {
        if (checkUserById(userId) && checkUserById(friendId)) {
            String getUserStatusQuery = "SELECT * from FRIENDS WHERE user_id = ?";
            try {
                Boolean userStatus = jdbcTemplate.queryForObject(getUserStatusQuery, Boolean.class, userId);
                if (Boolean.FALSE.equals(userStatus)) {
                    return false;
                } else {
                    throw new RuntimeException("Заявка на добавления в друзья пользователя с id "
                            + friendId + " уже существует!");
                }
            } catch (EmptyResultDataAccessException e) {
                return true;
            }
        }
        throw new RuntimeException("Ошибка добавления в друзья! Пользователя с id " + userId
                + " или с id " + friendId + " не существует!");
    }

    private User getRowMapperUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setLogin(resultSet.getString("login"));
        user.setEmail(resultSet.getString("email"));
        user.setBirthday(LocalDate.parse(Objects.requireNonNull(resultSet.getString("birthday"))));
        return user;
    }
}
