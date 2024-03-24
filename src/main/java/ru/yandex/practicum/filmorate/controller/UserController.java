package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDtoMapper;
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
    private final UserDtoMapper userDtoTransfer;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.addUser(userDtoTransfer.dtoToUser(userDto));
        log.info(String.format("Пользователь %s добавлен!", user.getName()));
        return userDtoTransfer.userToDto(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.updateUser(userDtoTransfer.dtoToUser(userDto));
        log.info(String.format("Пользователь %s обновлён!", user.getName()));
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
        log.info(String.format("Получение пользователя с %d с id", id));
        return userDtoTransfer.userToDto(userService.getUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info(String.format("Пользователь с id %d стал другом пользователя с id %d", id, friendId));
        userService.becomeToFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info(String.format("Пользователь с id %d удалил из друзей пользователя с id %d", id, friendId));
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable int id) {
        log.info(String.format("Получить всех друзей пользователя с id %d", id));
        return userService.getUserFriends(id)
                .stream()
                .map(userDtoTransfer::userToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info(String.format("Получить общих друзей пользователя с id %d и другого пользователя с id %d", id, otherId));
        return userService.getCommonsFriend(id, otherId)
                .stream()
                .map(userDtoTransfer::userToDto)
                .collect(Collectors.toList());
    }
}
