package ru.practicum.service;

import ru.practicum.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createNewEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(UpdateEventUserRequest eventUserRequest, Long userId, Long eventId);

    List<ParticipationRequestDto> getPartisipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest,
                                                        Long userId, Long eventId);

    List<EventFullDto> eventAdminSearch(List<Integer> users, List<Integer> states,
                                        List<Integer> categories, String rangeStart,
                                        String rangeEnd, Integer from, Integer size);

    EventFullDto updateAdminEvent(UpdateEventAdminRequest updateEventAdminRequest, Long eventId);

    List<EventShortDto> eventSearch(String text, List<Integer> categories,
                                    Boolean paid, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, Boolean onlyAvailable,
                                    String sort, Integer from, Integer size);

    EventFullDto getEventById(Long id);
}
