package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventTypeEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedStorage feedStorage;

    private static final String INSERT_USER = "INSERT INTO users (email, name, birthday, login) VALUES (?,?,?,?)";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_USER_FROM_FRIEND = "DELETE FROM friends WHERE user_id = ?";
    private static final String DELETE_FROM_FRIEND = "DELETE FROM friends WHERE friend_id = ?";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ? WHERE id = ?";
    private static final String SELECT_USER = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_FRIENDS = "SELECT f.friend_id, f.user_id FROM friends AS f WHERE f.user_id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_FRIENDS = "INSERT INTO friends (user_id, friend_id) VALUES (?,?)";
    private static final String DELETE_BY_USER_AND_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_EXISTS_USER_BY_ID = "SELECT EXISTS(SELECT 1 FROM USERS WHERE id = ?)";
    private static final String SELECT_EXISTS_USER_BY_USER = "SELECT EXISTS(SELECT 1 FROM USERS WHERE EMAIL = ? OR LOGIN = ?)";
    private static final String SELECT_EXISTS_FRIEND_BY_USER_AND_FRIEND_ID = "SELECT EXISTS(SELECT 1 FROM FRIENDS WHERE user_id = ? AND friend_id = ?)";
    private static final String SELECT_EXISTS_FRIEND_BY_USER_ID = "SELECT EXISTS(SELECT 1 FROM FRIENDS WHERE user_id = ?)";


    @Override
    public User add(User user) {
        if (!checkUser(user)) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(INSERT_USER, new String[]{"id"});
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
            jdbcTemplate.update(DELETE_USER_FROM_FRIEND, user.getId());
            jdbcTemplate.update(DELETE_FROM_FRIEND, user.getId());
            jdbcTemplate.update(DELETE_USER, user.getId());
            return user;
        }
        throw new UserNotFoundException("Ошибка удаления пользователя!");
    }

    @Override
    public User update(User user) {
        if (checkUserById(user.getId())) {
            jdbcTemplate.update(UPDATE_USER, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday(), user.getId());
            return user;
        }
        throw new UserNotFoundException("Ошибка обновления пользователя!");
    }


    @Override
    public User get(long id) {
        if (checkUserById(id)) {
            User user = jdbcTemplate.queryForObject(SELECT_USER, this::getRowMapperUser, id);
            if (checkFriends(id)) {
                List<Friends> friends = jdbcTemplate.query(SELECT_FRIENDS, this::getRowMapperFriends, id);
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
        return jdbcTemplate.query(SELECT_ALL_USERS, this::getRowMapperUser);
    }

    public void addFriend(long userId, long friendId) {
        if (checkUserById(userId) && checkUserById(friendId)) {
            boolean user = checkFriendsById(userId, friendId);
            if (!user) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(INSERT_FRIENDS, new String[]{"id"});
                    stmt.setLong(1, userId);
                    stmt.setLong(2, friendId);
                    return stmt;
                });
                feedStorage.create(new Feed(userId, EventTypeEnum.FRIEND, OperationEnum.ADD, friendId));
            } else {
                throw new RuntimeException("Заявка пользователя с id " + userId + " в друзья к пользователю с id "
                        + friendId + "уже существует!");
            }
        } else {
            throw new UserNotFoundException("Для добавления в друзья пользователи не найдены!");
        }
    }

    public void removeFriend(long userId, long friendId) {
        if (checkUserById(userId) && checkUserById(friendId)) {
            jdbcTemplate.update(DELETE_BY_USER_AND_FRIEND, userId, friendId);
            feedStorage.create(new Feed(userId, EventTypeEnum.FRIEND, OperationEnum.REMOVE, friendId));
        } else {
            throw new UserNotFoundException("Для добавления в друзья пользователи не найдены!");
        }
    }

    public boolean checkUserById(long id) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_USER_BY_ID, Boolean.class, id);
    }

    private boolean checkUser(User user) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_USER_BY_USER, Boolean.class, user.getEmail(), user.getLogin());
    }

    private boolean checkFriendsById(long userId, long friendId) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_FRIEND_BY_USER_AND_FRIEND_ID, Boolean.class, userId, friendId);
    }

    private boolean checkFriends(long userId) {
        return jdbcTemplate.queryForObject(SELECT_EXISTS_FRIEND_BY_USER_ID, Boolean.class, userId);
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
        return friends;
    }
}
