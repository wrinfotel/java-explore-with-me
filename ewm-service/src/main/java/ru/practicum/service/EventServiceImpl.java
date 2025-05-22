package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.*;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public EventFullDto createNewEvent(NewEventDto newEventDto, Long userId) {
        return null;
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateUserEvent(UpdateEventUserRequest eventUserRequest, Long userId, Long eventId) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getPartisipationRequests(Long userId, Long eventId) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest,
                                                               Long userId, Long eventId) {
        return null;
    }

    @Override
    public List<EventFullDto> eventAdminSearch(List<Integer> users, List<Integer> states,
                                               List<Integer> categories, String rangeStart,
                                               String rangeEnd, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public EventFullDto updateAdminEvent(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        return null;
    }

    @Override
    public List<EventShortDto> eventSearch(String text, List<Integer> categories,
                                           Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable,
                                           String sort, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public EventFullDto getEventById(Long id) {
        return null;
    }
}
