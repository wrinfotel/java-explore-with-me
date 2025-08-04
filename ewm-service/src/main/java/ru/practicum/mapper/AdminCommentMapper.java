package ru.practicum.mapper;

import ru.practicum.dto.EventAdminCommentDto;
import ru.practicum.model.AdminComment;

import java.time.format.DateTimeFormatter;

public class AdminCommentMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventAdminCommentDto toAdminCommentDto(AdminComment comment) {
        return EventAdminCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventMapper.toShortDto(comment.getEvent()))
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt().format(DATE_TIME_FORMATTER))
                .build();
    }
}
