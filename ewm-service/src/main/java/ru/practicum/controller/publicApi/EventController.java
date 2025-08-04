package ru.practicum.controller.publicApi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final StatClient statClient;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        sendStat(request);
        return eventService.eventSearch(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        sendStat(request);
        return eventService.getEventById(id);
    }

    private void sendStat(HttpServletRequest httpServletRequest) {
        try {
            HitCreateRequestDto requestDto = new HitCreateRequestDto();
            requestDto.setApp("service");
            requestDto.setIp(httpServletRequest.getRemoteAddr());
            requestDto.setUri(httpServletRequest.getRequestURI());
            requestDto.setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            statClient.saveEvent(requestDto);
        } catch (Exception ignore) {
        }

    }
}
