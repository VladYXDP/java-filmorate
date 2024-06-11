package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventTypeEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_FEED = "INSERT INTO feeds (timestamp, user_id, event_type, operation, entity_id) VALUES (?,?,?,?,?)";
    private static final String SELECT_FEED = "SELECT * FROM feeds WHERE user_id = ?";

    @Override
    public void create(Feed feed) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_FEED, new String[]{"id"});
            stmt.setTimestamp(1, Timestamp.valueOf(feed.getTimestamp()));
            stmt.setLong(2, feed.getUserId());
            stmt.setString(3, feed.getEventType().name());
            stmt.setString(4, feed.getOperation().name());
            stmt.setLong(5, feed.getEntityId());
            return stmt;
        });
    }

    @Override
    public List<Feed> get(long userId) {
        return jdbcTemplate.query(SELECT_FEED, this::getRowMapperFeed, userId);
    }

    private Feed getRowMapperFeed(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(resultSet.getLong("id"));
        feed.setUserId(resultSet.getLong("user_id"));
        feed.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
        feed.setEventType(EventTypeEnum.valueOf(resultSet.getString("event_type")));
        feed.setOperation(OperationEnum.valueOf(resultSet.getString("operation")));
        feed.setEntityId(resultSet.getLong("entity_id"));
        return feed;
    }
}
