package ru.practicum.controller.privateApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(name = "from") Integer from,
                                             @RequestParam(name = "size") Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@RequestBody NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        return eventService.createNewEvent(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@RequestBody UpdateEventUserRequest eventUserRequest,
                                        @PathVariable Long userId,
                                        @PathVariable Long eventId) {
        return eventService.updateUserEvent(eventUserRequest, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return eventService.getPartisipationRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult setRequestsStatus(@RequestBody EventRequestStatusUpdateRequest updateRequest,
                                                            @PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        return eventService.updateRequestsStatus(updateRequest, userId, eventId);
    }
}
