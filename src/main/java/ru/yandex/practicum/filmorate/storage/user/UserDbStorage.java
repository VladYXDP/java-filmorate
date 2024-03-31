package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        checkUser(user);
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

    @Override
    public User delete(User user) {
        checkUserById(user.getId());
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        String deleteFriends = "DELETE FROM friends WHERE user_id = ?";
        String deleteFromFriends = "DELETE FROM friends WHERE friend_id = ?";
        jdbcTemplate.update(deleteQuery, user.getId());
        jdbcTemplate.update(deleteFriends, user.getId());
        jdbcTemplate.update(deleteFromFriends, user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        checkUserById(user.getId());
        String updateQuery = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ?";
        jdbcTemplate.update(updateQuery, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        return user;
    }


    @Override
    public User get(long id) {
        String getQuery = "SELECT * FROM users WHERE id = ?";
        String getFriendsQuery = "SELECT f.friend_id, f.user_id, f.status FROM friends AS f WHERE f.user_id = ?";
        User user = jdbcTemplate.queryForObject(getQuery, this::getRowMapperUser, id);
        if (user != null) {
            List<Friends> friends = jdbcTemplate.query(getFriendsQuery, this::getRowMapperFriends);
            List<Long> friendIds = friends.stream().map(Friends::getFriendId).collect(Collectors.toList());
            user.setFriends(new HashSet<>(friendIds));
            return user;
        } else {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден!");
        }
    }

    @Override
    public List<User> getAllUser() {
        String getAllQuery = "SELECT * FROM users";
        return jdbcTemplate.query(getAllQuery, this::getRowMapperUser);
    }

    public void addFriend(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);
        String getFriendsQuery = "SELECT f.user_id, f.friend_id, f.status FROM friends AS f WHERE f.user_id = ?";
        String addUserToFriendQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?,?,?)";
        String updateFriendStatus = "UPDATE friends SET status = true WHERE user_id = ? AND friend_id = ?";
        Friends user = jdbcTemplate.queryForObject(getFriendsQuery, this::getRowMapperFriends, userId);
        Friends friend = jdbcTemplate.queryForObject(getFriendsQuery, this::getRowMapperFriends, friendId);
        if (user == null) {
            if (friend == null) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(addUserToFriendQuery, new String[]{"id"});
                    stmt.setLong(1, userId);
                    stmt.setLong(2, friendId);
                    stmt.setBoolean(3, false);
                    return stmt;
                });
            } else {
                jdbcTemplate.update(updateFriendStatus, friendId, userId);
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(addUserToFriendQuery, new String[]{"id"});
                    stmt.setLong(1, userId);
                    stmt.setLong(2, friendId);
                    stmt.setBoolean(3, true);
                    return stmt;
                });
            }
        }
    }

    public void removeFriend(long userId, long friendId) {
        String deleteFriendsQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        String updateFriends = "UPDATE friends SET status = false WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteFriendsQuery, userId, friendId);
        jdbcTemplate.update(updateFriends, friendId, userId);
    }

    private void checkUserById(long id) {
        String existsUserByIdQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE id = ?)";
        boolean userExist = jdbcTemplate.queryForObject(existsUserByIdQuery, Boolean.class, id);
        if (!userExist) {
            throw new RuntimeException("Ошибка добавления в друзья! Пользователя с id " + id + " не существует!");
        }
    }

    private void checkUser(User user) {
        String existsUserQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE EMAIL = ? OR LOGIN = ?)";
        boolean userExist = jdbcTemplate.queryForObject(existsUserQuery, Boolean.class, user.getEmail(), user.getLogin());
        if (!userExist) {
            throw new UserAlreadyExistException("Пользователь с login " + user.getLogin() + " или email "
                    + user.getEmail() + " уже существует!");
        }
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

    private Friends getRowMapperFriends(ResultSet resultSet, int rowNum) throws SQLException {
        Friends friends = new Friends();
        friends.setFriendId(resultSet.getLong("friend_id"));
        friends.setUserId(resultSet.getLong("user_id"));
        friends.setStatus(resultSet.getBoolean("status"));
        return friends;
    }
}
