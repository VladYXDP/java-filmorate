package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDtoMapper;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorStorage;
    private final DirectorDtoMapper directorDtoMapper;

    public DirectorDto addDirector(DirectorDto directorDto) {
        return directorDtoMapper.toDto(directorStorage.addDirector(directorDtoMapper.toEntity(directorDto)));
    }

    public List<DirectorDto> getAllDirectors() {
        return directorDtoMapper.toDtoList(directorStorage.getDirectors());
    }

    public DirectorDto getDirectorById(long id) {
        return directorDtoMapper.toDto(directorStorage.getDirector(id));
    }

    public DirectorDto updateDirector(DirectorDto directorDto) {
        return directorDtoMapper.toDto(directorStorage.updateDirector(directorDtoMapper.toEntity(directorDto)));
    }

    public DirectorDto deleteDirector(long id) {
        return directorDtoMapper.toDto(directorStorage.deleteDirector(id));
    }
}
