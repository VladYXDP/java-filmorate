package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserIsNullException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendDbStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.SlopeOneRecommender;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final FilmStorage filmStorage;
    private final RecommendDbStorage recommendDbStorage;
    private final SlopeOneRecommender slopeOneRecommender;

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
        getUserById(userId).getFriends().forEach(it -> friend.add(getUserById(it)));
        return friend;
    }

    public List<Feed> getFeed(long userId) {
        userStorage.get(userId);
        return feedStorage.get(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUser();
    }

    public void deleteUserByID(Long userId) {
        userStorage.deleteUserByID(userId);
    }

    public List<Film> recommendFilms(Long userId) {
        Map<Long, Map<Long, Integer>> data = recommendDbStorage.getUserLikes();
        //Строим матрицы различий и частот
        slopeOneRecommender.buildMatrices(data);
        //Получаем лайки конкретного пользователя
        Map<Long, Integer> userRatings = data.get(userId);
        return slopeOneRecommender.recommend(userRatings, filmStorage);
    }
}
