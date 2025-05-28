package ru.practicum.stat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.stat.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final HitService hitService;

    private static final String DATE = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitResponseDto saveHit(@RequestBody HitCreateRequestDto requestHit) {
        return hitService.createHit(requestHit);
    }

    @GetMapping("/stats")
    public List<StatResponseDto> getStats(@RequestParam(value = "start")
                                          @DateTimeFormat(pattern = DATE) LocalDateTime start,
                                          @RequestParam(value = "end")
                                          @DateTimeFormat(pattern = DATE) LocalDateTime end,
                                          @RequestParam(value = "uris", required = false) List<String> uris,
                                          @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        return hitService.getStatistics(start, end, uris, unique);

    }
}
