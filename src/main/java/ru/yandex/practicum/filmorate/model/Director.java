package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Director {

    private int id;
    private String name;

    public Map<String, Object> toMap() {
        return Map.of("id", id, "name", name);
    }
}
