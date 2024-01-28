package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private static int index = 0;

    public User addUser(User user) {
        if (user != null && !users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            user.setId(++index);
            return users.put(user.getId(), user);
        } else {
            throw new RuntimeException("Ошибка добавления пользователя");
        }
    }

    public User updateUser(User user) {
        if (user != null && users.containsKey(user.getId())) {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            return users.replace(user.getId(), user);
        } else {
            throw new RuntimeException("Ошибка обновления пользователя");
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}