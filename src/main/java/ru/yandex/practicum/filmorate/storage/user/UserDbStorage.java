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
        throw new UserAlreadyExistException("Пользователь с email " + user.getEmail() + " и login " + user.getLogin() +
                " уже существует");
    }

    @Override
    public User delete(User user) {
        if (checkUserById(user.getId())) {
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            String deleteFriends = "DELETE FROM friends WHERE user_id = ?";
            String deleteFromFriends = "DELETE FROM friends WHERE friend_id = ?";
            jdbcTemplate.update(deleteQuery, user.getId());
            jdbcTemplate.update(deleteFriends, user.getId());
            jdbcTemplate.update(deleteFromFriends, user.getId());
            return user;
        }
        throw new UserNotFoundException("Ошибка удаления пользователя!");
    }

    @Override
    public User update(User user) {
        if (checkUserById(user.getId())) {
            String updateQuery = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ?";
            jdbcTemplate.update(updateQuery, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
            return user;
        }
        throw new UserNotFoundException("Ошибка обновления пользователя!");
    }


    @Override
    public User get(long id) {
        String getQuery = "SELECT * FROM users WHERE id = ?";
        String getFriendsQuery = "SELECT f.friend_id, f.user_id, f.status FROM friends AS f WHERE f.user_id = ?";
        User user = jdbcTemplate.queryForObject(getQuery, this::getRowMapperUser, id);
        if (user != null) {

            if (checkFriends(id)) {
                List<Friends> friends = jdbcTemplate.query(getFriendsQuery, this::getRowMapperFriends);
                List<Long> friendIds = friends.stream().map(Friends::getFriendId).collect(Collectors.toList());
                user.setFriends(new HashSet<>(friendIds));
            } else {
                user.setFriends(new HashSet<>());
            }
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
        if (checkUserById(userId) && checkUserById(friendId)) {
            String addUserToFriendQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?,?,?)";
            String updateFriendStatus = "UPDATE friends SET status = true WHERE user_id = ? AND friend_id = ?";
            boolean user = checkFriendsById(userId, friendId);
            boolean friend = checkFriendsById(friendId, userId);
            if (!user) {
                if (!friend) {
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
            } else {
                throw new RuntimeException("Заявка пользователя с id " + userId + " в друзья к пользователю с id "
                        + friendId + "уже существует!");
            }
        } else {
            throw new UserNotFoundException("Для добавления в друзья пользователи не найдены!");
        }
    }

    public void removeFriend(long userId, long friendId) {
        String deleteFriendsQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        String updateFriends = "UPDATE friends SET status = false WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteFriendsQuery, userId, friendId);
        jdbcTemplate.update(updateFriends, friendId, userId);
    }

    private boolean checkUserById(long id) {
        String existsUserByIdQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE id = ?)";
        return jdbcTemplate.queryForObject(existsUserByIdQuery, Boolean.class, id);
    }

    private boolean checkUser(User user) {
        String existsUserQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE EMAIL = ? OR LOGIN = ?)";
        return jdbcTemplate.queryForObject(existsUserQuery, Boolean.class, user.getEmail(), user.getLogin());
    }

    private boolean checkFriendsById(long userId, long friendId) {
        String existsFriendQuery = "SELECT EXISTS(SELECT 1 FROM FRIENDS WHERE user_id = ? AND friend_id = ?)";
        return jdbcTemplate.queryForObject(existsFriendQuery, Boolean.class, userId, friendId);
    }

    private boolean checkFriends(long userId) {
        String checkFriendQuery = "SELECT EXISTS(SELECT 1 FROM FRIENDS WHERE user_id = ? AND status = true)";
        return jdbcTemplate.queryForObject(checkFriendQuery, Boolean.class, userId);
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
        Friends friends = null;
        if (resultSet.next()) {
            friends = new Friends();
            friends.setFriendId(resultSet.getLong("friend_id"));
            friends.setUserId(resultSet.getLong("user_id"));
            friends.setStatus(resultSet.getBoolean("status"));
        }
        return friends;
    }
}
