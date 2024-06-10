package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.enums.EventTypeEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;

import java.time.LocalDateTime;

@Getter
@Setter
public class Feed {
    private Long eventId;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Long userId;
    private EventTypeEnum eventType;
    private OperationEnum operation;
    private Long entityId;

    public Feed(Long userId, EventTypeEnum eventType, OperationEnum operation, Long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
