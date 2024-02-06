package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private static int index = 0;

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
            throw new UserNotFoundException("Ошибка обновления данных о пользователе с email - " + user.getEmail() + "" +
                    "! Пользователь не найден!");
        }
    }

    @Override
    public Map<Integer, User> getAllUser() {
        return users;
    }
}
