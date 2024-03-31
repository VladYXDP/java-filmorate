package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

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

    public User deleteUser(User user) {
        if (user != null) {
            return userStorage.delete(user);
        } else {
            throw new UserIsNullException("Ошибка удаления пользователя!");
        }
    }

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

//    public void becomeToFriend(long userId, long friendId) {
//        if (friendId > 0) {
//            if (isNotFriends(userId, friendId)) {
//                getUserById(userId).getFriends().add(friendId);
//                getUserById(friendId).getFriends().add(userId);
//            } else {
//                throw new UserAlreadyExistException("Пользователи уже являются друзьями!");
//            }
//        } else {
//            throw new UserNotFoundException(String.format("Неверный формат параметра friendId %d", friendId));
//        }
//    }

//    public void removeFriend(long userId, long friendId) {
//        User user = getUserById(userId);
//        User friend = getUserById(friendId);
//        if (isNotFriends(userId, friendId)) {
//            throw new UserNotFoundException(("Ошибка удаления из друзей! Пользователи не были друзьями!"));
//        } else {
//            user.getFriends().remove(friend.getId());
//            friend.getFriends().remove(user.getId());
//        }
//    }

    public List<User> getCommonsFriend(long id, long otherId) {
        List<User> users = new ArrayList<>();
        Set<Long> userFriends = new HashSet<>(getUserById(id).getFriends());
        userFriends.retainAll(getUserById(otherId).getFriends());
        for (Long friendId : userFriends) {
            users.add(getUserById(friendId));
        }
        return users;
    }

    public User getUserById(long userId) {
        return userStorage.get(userId);
    }

    public List<User> getUserFriends(long userId) {
        List<User> friend = new ArrayList<>();
        getUserById(userId).getFriends().forEach(it -> friend.add(getUserById(it)));
        if (friend.isEmpty()) {
            throw new UserNotFoundException(String.format("Список друзей у пользователя с id %d пуст!", userId));
        }
        return friend;
    }

    private boolean isNotFriends(long user, long friend) {
        return !getUserById(user).getFriends().contains(friend);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUser();
    }
}
