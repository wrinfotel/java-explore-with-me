package ru.practicum.controller.privateApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        return eventService.createNewEvent(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@Valid @RequestBody UpdateEventUserRequest eventUserRequest,
                                        @PathVariable Long userId,
                                        @PathVariable Long eventId) {
        return eventService.updateUserEvent(eventUserRequest, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/resend")
    public EventFullDto resendEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        return eventService.resendEvent(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return eventService.getPartisipationRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult setRequestsStatus(@Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
                                                            @PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        return eventService.updateRequestsStatus(updateRequest, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/admin-comment")
    public List<EventAdminCommentDto> getEventAdminComment(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        return eventService.getAdminComment(userId, eventId);
    }
}
