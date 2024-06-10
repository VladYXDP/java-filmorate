package ru.yandex.practicum.filmorate.dto.feed;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

@Component
public class FeedDtoTransfer {

    public FeedDto feedToDto (Feed feed) {
        FeedDto dto = new FeedDto();
        dto.setTimestamp(feed.getTimestamp());
        dto.setUserId(feed.getUserId());
        dto.setEventType(feed.getEventType().name());
        dto.setOperation(feed.getOperation().name());
        dto.setEventId(feed.getEventId());
        dto.setEntityId(feed.getEntityId());
        return dto;
    }
}
