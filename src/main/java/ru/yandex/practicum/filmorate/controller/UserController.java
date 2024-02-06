package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserDtoMapper;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoTransfer;

    @ExceptionHandler(UserAlreadyExistException.class)
    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.addUser(userDtoTransfer.dtoToUser(userDto));
        log.info("Пользователь " + user.getName() + " добавлен!");
        return userDtoTransfer.userToDto(user);
    }

    @ExceptionHandler(UserNotFoundException.class)
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

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        return userDtoTransfer.userToDto(userService.getUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.becomeToFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friend/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable int id) {
        return userService.getAllUsers().get(id).getFriends()
                .stream()
                .map(userDtoTransfer::userToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonsFriend(id, otherId)
                .stream()
                .map(userDtoTransfer::userToDto)
                .collect(Collectors.toList());
    }
}
