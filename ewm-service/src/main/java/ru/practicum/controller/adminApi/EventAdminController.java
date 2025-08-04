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
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventStatus> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return eventService.eventAdminSearch(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/pending-events")
    public List<EventFullDto> getPendingEvents(@RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
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
