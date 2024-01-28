package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        try {
            userService.addUser(user);
            log.info("Пользователь " + user.getName() + " добавлен!");
        } catch (ValidationException e) {
            log.error("Ошибка валидации! " + e.getMessage());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        try {
            userService.updateUser(user);
            log.info("Пользователь " + user.getName() + " обновлён!");
        } catch (ValidationException e) {
            log.error("Ошибка валидации!");
        }
        return user;
    }

    @GetMapping
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }
}
