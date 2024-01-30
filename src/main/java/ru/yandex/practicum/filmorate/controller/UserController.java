package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserDtoTransfer;
import ru.yandex.practicum.filmorate.exception.user.AddUserException;
import ru.yandex.practicum.filmorate.exception.user.UpdateUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserDtoTransfer userDtoTransfer;

    @ExceptionHandler(AddUserException.class)
    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.addUser(userDtoTransfer.dtoToUser(userDto));
        log.info("Пользователь " + user.getName() + " добавлен!");
        return userDtoTransfer.userToDto(user);
    }

    @ExceptionHandler(UpdateUserException.class)
    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.updateUser(userDtoTransfer.dtoToUser(userDto));
        log.info("Пользователь " + user.getName() + " обновлён!");
        return userDtoTransfer.userToDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUser() {
        log.info("Получен список всех пользователей!");
        return userService.getAllUsers()
                .stream()
                .map(userDtoTransfer::userToDto)
                .collect(Collectors.toList());
    }
}
