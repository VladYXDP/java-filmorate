package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final Map<Long, List<User>> deletedUserFriendsCache = new HashMap<>();

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
        if (!deletedUserFriendsCache.containsKey(userId)) {
            getUserById(userId).getFriends().forEach(it -> friend.add(getUserById(it)));
            return friend;
        } else {
            return deletedUserFriendsCache.get(userId);
        }
    }

    public List<Feed> getFeed(long userId) {
        return feedStorage.get(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUser();
    }

    public void deleteUserByID(Long userId) {
        deletedUserFriendsCache.put(userId, getUserFriends(userId));
        userStorage.deleteUserByID(userId);
    }
}
