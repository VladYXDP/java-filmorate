package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private long id;
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
