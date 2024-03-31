package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long index = 0;

    @Override
    public User add(User user) {
        user.setId(++index);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            return user;
        } else {
            throw new UserNotFoundException("Ошибка удаления пользователя с id - " + user.getId() + "" +
                    "! Пользователь не найден!");
        }
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            users.replace(user.getId(), oldUser, user);
            return oldUser;
        } else {
            throw new UserNotFoundException("Ошибка обновления данных о пользователе с id - " + user.getId() + "" +
                    "! Пользователь не найден!");
        }
    }

    @Override
    public User get(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден!", id));
        }
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public void removeFriend(long userId, long friendId) {

    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(users.values());
    }
}
