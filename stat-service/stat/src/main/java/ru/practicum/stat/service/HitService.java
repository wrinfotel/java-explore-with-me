package ru.practicum.stat.service;

import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.dto.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    HitResponseDto createHit(HitCreateRequestDto hit);

    List<StatResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
