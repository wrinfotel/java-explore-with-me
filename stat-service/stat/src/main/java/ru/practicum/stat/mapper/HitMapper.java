package ru.practicum.stat.mapper;

import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.stat.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    public static HitResponseDto toHitDto(Hit hit) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return HitResponseDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp().format(dateTimeFormatter))
                .build();
    }

    public static Hit toHit(HitCreateRequestDto hitCreateRequestDto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Hit.builder()
                .app(hitCreateRequestDto.getApp())
                .uri(hitCreateRequestDto.getUri())
                .ip(hitCreateRequestDto.getIp())
                .timestamp(LocalDateTime.parse(hitCreateRequestDto.getTimestamp(), dateTimeFormatter))
                .build();
    }
}
