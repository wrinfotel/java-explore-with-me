package ru.practicum.stat.mapper;

import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.stat.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HitResponseDto toHitDto(Hit hit) {
        return HitResponseDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp().format(DATE_TIME_FORMATTER))
                .build();
    }

    public static Hit toHit(HitCreateRequestDto hitCreateRequestDto) {
        return Hit.builder()
                .app(hitCreateRequestDto.getApp())
                .uri(hitCreateRequestDto.getUri())
                .ip(hitCreateRequestDto.getIp())
                .timestamp(LocalDateTime.parse(hitCreateRequestDto.getTimestamp(), DATE_TIME_FORMATTER))
                .build();
    }
}
