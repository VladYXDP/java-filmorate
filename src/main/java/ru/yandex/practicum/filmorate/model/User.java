package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validate.annotation.BirthdayValidation;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private int id;
    @NonNull
    @Email
    private String email;
    private String name;
    @NonNull
    @NotBlank
    @Pattern(regexp = "([\\S]+)")
    private String login;
    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @BirthdayValidation
    private LocalDate birthday;
}
