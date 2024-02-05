package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) {
        if (user != null) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            userStorage.add(user);
            return user;
        } else {
            throw new UserIsNullException("Ошибка добавления пользователя!");
        }
    }

    public User updateUser(User user) {
        if (user != null) {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            userStorage.update(user);
            return user;
        } else {
            throw new UserIsNullException("Ошибка обновления пользователя!");
        }
    }

    public User delete(User user) {
        if (user != null) {
            return userStorage.delete(user);
        } else {
            throw new UserIsNullException("Ошибка удаления пользователя!");
        }
    }

    public void becomeToFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (isNotFriends(user, friend)) {
            user.getFriends().add(friend);
            friend.getFriends().add(friend);
        } else {
            throw new UserAlreadyExistException("Пользователи уже являются друзьями!");
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (isNotFriends(user, friend)) {
            throw new UserNotFoundException(("Ошибка удаления из друзей! Пользователи не были друзьями!"));
        } else {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);
        }
    }

    public List<User> getCommonsFriend(User user, User friend) {
        Set<User> userFriends = new HashSet<>(user.getFriends());
        userFriends.retainAll(friend.getFriends());
        return new ArrayList<>(userFriends);
    }

    private User getUserById(int userId) {
        if (userStorage.getAllUser().containsKey(userId)) {
            return userStorage.getAllUser().get(userId);
        } else {
            throw new UserNotFoundException("Пользователя с id " + userId + " не существует!");
        }
    }

    private boolean isNotFriends(User user, User friend) {
        return !user.getFriends().contains(friend);
    }
}
