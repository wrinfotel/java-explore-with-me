package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.stat.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.practicum.dto.StatResponseDto(h.app, h.uri, count(h.uri)) from Hit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.uri, h.app " +
            "order by count(h.uri) desc")
    List<StatResponseDto> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatResponseDto(h.app, h.uri, count(distinct h.uri)) from Hit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.uri, h.app " +
            "order by count(distinct h.uri)")
    List<StatResponseDto> findAllByTimestampBetweenUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatResponseDto(h.app, h.uri, count(distinct h.uri)) from Hit as h " +
            "where h.uri IN (?3) and h.timestamp between ?1 and ?2 " +
            "group by h.uri, h.app " +
            "order by count(distinct h.uri)")
    List<StatResponseDto> findAllByTimestampBetweenUniqueWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.StatResponseDto(h.app, h.uri, count(h.uri)) from Hit as h " +
            "where h.uri IN (?3) and h.timestamp between ?1 and ?2 " +
            "group by h.uri, h.app " +
            "order by count(h.uri) desc")
    List<StatResponseDto> findAllByTimestampBetweenWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
