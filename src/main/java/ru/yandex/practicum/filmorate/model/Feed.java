package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.enums.EventTypeEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Feed {
    private LocalDateTime timestamp;
    private Long userId;
    private EventTypeEnum eventType;
    private OperationEnum operation;
    private Long eventId;
    private Long entityId;
}
