package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User add(User user);

    User delete(User user);

    User update(User user);

    User get(long id);

    void addFriend(long userId, long friendId);

    List<User> getAllUser();
}
