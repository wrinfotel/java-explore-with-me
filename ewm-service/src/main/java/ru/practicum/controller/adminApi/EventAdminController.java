package ru.practicum.controller.adminApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                        @RequestParam(name = "states", required = false) List<EventStatus> states,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                        @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return eventService.eventAdminSearch(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/pending-events")
    public List<EventFullDto> getPendingEvents(@RequestParam(name = "from", defaultValue = "0",
                                                       required = false) Integer from,
                                               @RequestParam(name = "size", defaultValue = "10",
                                                       required = false) Integer size) {
        return eventService.eventAdminSearch(null, List.of(EventStatus.PENDING),
                null, null, null, from, size);
    }

    @PostMapping("add-comment")
    @ResponseStatus(HttpStatus.CREATED)
    public EventAdminCommentDto addAdminCommentToEvent(@Valid @RequestBody NewEventAdminCommentDto eventAdminComment) {
        return eventService.addAdminCommentAndChangeStatus(eventAdminComment);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventUserRequest updateEventAdminRequest,
                                    @PathVariable Long eventId) {
        return eventService.updateAdminEvent(updateEventAdminRequest, eventId);
    }
}
