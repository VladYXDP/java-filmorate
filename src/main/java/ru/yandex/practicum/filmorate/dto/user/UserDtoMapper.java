package ru.yandex.practicum.filmorate.dto.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserDtoMapper {

    public User dtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setLogin(userDto.getLogin());
        user.setBirthday(userDto.getBirthday());
        return user;
    }

    public UserDto userToDto(User user) {
        if (user != null) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setLogin(user.getLogin());
            userDto.setName(user.getName());
            userDto.setBirthday(user.getBirthday());
            userDto.setEmail(user.getEmail());
            return userDto;
        }
        return null;
    }
}
