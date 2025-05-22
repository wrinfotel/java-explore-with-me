package ru.practicum.controller.adminApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(name = "users") List<Integer> users,
                                        @RequestParam(name = "states") List<Integer> states,
                                        @RequestParam(name = "categories") List<Integer> categories,
                                        @RequestParam(name = "rangeStart") String rangeStart,
                                        @RequestParam(name = "rangeEnd") String rangeEnd,
                                        @RequestParam(name = "from") Integer from,
                                        @RequestParam(name = "size") Integer size) {
        return eventService.eventAdminSearch(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                    @PathVariable Long eventId) {
        return eventService.updateAdminEvent(updateEventAdminRequest, eventId);
    }
}
