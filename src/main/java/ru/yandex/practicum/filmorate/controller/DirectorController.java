package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public DirectorDto addDirector(@RequestBody final DirectorDto directorDto) {
        log.info("Поступил запрос на добавление режиссера {}", directorDto.getName());
        DirectorDto createdDirector = directorService.addDirector(directorDto);
        log.debug("Режиссер добавлен {}", createdDirector);
        return createdDirector;
    }

    @GetMapping
    public List<DirectorDto> getAllDirectors() {
        log.info("Получен список всех режиссеров!");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable long id) {
        log.info("Получен режиссер с id {}", id);
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public DirectorDto updateDirector(@RequestBody @Valid final DirectorDto directorDto) {
        log.info("Поступил запрос на обновление режиссера {}", directorDto.getName());
        DirectorDto updatedDirector = directorService.updateDirector(directorDto);
        log.debug("Режиссер обновлен {}", updatedDirector);
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    public DirectorDto deleteDirector(@PathVariable long id) {
        log.info("Поступил запрос на удаление режиссера c id {}", id);
        DirectorDto deletedDirector = directorService.deleteDirector(id);
        log.debug("Режиссер удален {}", deletedDirector);
        return deletedDirector;
    }
}
