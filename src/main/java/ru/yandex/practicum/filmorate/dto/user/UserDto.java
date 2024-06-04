package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validate.annotation.BirthdayValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class UserDto {

    private long id;
    @NotNull
    @Email
    private String email;
    private String name;
    @NotBlank
    @Pattern(regexp = "([\\S]+)")
    private String login;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @BirthdayValidation
    private LocalDate birthday;
}
