package ru.practicum.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatClient;
import ru.practicum.dto.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.AdminCommentMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final ParticipationRequestRepository requestRepository;

    private final StatClient statClient;

    private final AdminCommentRepository adminCommentRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepository userRepository;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorId(userId, page).stream()
                .map(EventMapper::toShortDto).toList();
    }

    @Override
    public EventFullDto createNewEvent(NewEventDto newEventDto, Long userId) {
        Event newEvent = EventMapper.toEvent(newEventDto);
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        newEvent.setInitiator(user);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));
        newEvent.setCategory(category);
        Location newLocation = Location.builder()
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
        Location location = locationRepository.save(newLocation);
        newEvent.setLocation(location);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setUpdatedOn(LocalDateTime.now());
        newEvent.setState(EventStatus.PENDING);


        return EventMapper.toFullDto(eventRepository.save(newEvent));
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = checkIsUserEvent(userId, eventId);
        return EventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto updateUserEvent(UpdateEventUserRequest eventUserRequest, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("You can`t change this event");
        }
        if ((eventUserRequest.getEventDate() != null && LocalDateTime
                .parse(eventUserRequest.getEventDate(), DATE_TIME_FORMATTER)
                .isBefore(LocalDateTime.now().plusHours(2))) ||
                !event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("You can`t change this event");
        }
        updateEventFields(event, eventUserRequest);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getPartisipationRequests(Long userId, Long eventId) {
        checkIsUserEvent(userId, eventId);
        List<ParticipationRequest> requestList = requestRepository.findAllByEventId(eventId);
        return requestList.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest,
                                                               Long userId, Long eventId) {
        Event event = checkIsUserEvent(userId, eventId);
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return null;
        }
        long limitSize = 0;
        if (event.getParticipantRequests() != null) {
            limitSize = event.getParticipantRequests().stream()
                    .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED)).count();
        }
        if (limitSize >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        EventRequestStatusUpdateResult updated = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>()).build();
        for (ParticipationRequest request : requests) {
            if (updateRequest.getRequestIds().contains(request.getId())) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ConflictException("Request must have status PENDING");
                }
                if (event.getParticipantLimit() == null || limitSize < event.getParticipantLimit()) {
                    request.setStatus(updateRequest.getStatus());
                    if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                        updated.getConfirmedRequests()
                                .add(ParticipationRequestMapper
                                        .toParticipationRequestDto(requestRepository.save(request)));
                        limitSize++;
                    } else {
                        updated.getRejectedRequests()
                                .add(ParticipationRequestMapper
                                        .toParticipationRequestDto(requestRepository.save(request)));
                    }
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    updated.getRejectedRequests()
                            .add(ParticipationRequestMapper
                                    .toParticipationRequestDto(requestRepository.save(request)));
                }
            }
        }
        return updated;
    }

    @Override
    public List<EventFullDto> eventAdminSearch(List<Long> users, List<EventStatus> states,
                                               List<Long> categories, String rangeStart,
                                               String rangeEnd, Integer from, Integer size) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (users != null && !users.isEmpty()) {
            predicate.and(QEvent.event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicate.and(QEvent.event.state.in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categories));
        }
        if (rangeStart != null && !rangeStart.isEmpty() && !rangeStart.isBlank()) {
            predicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER)));
        }
        if (rangeEnd != null && !rangeEnd.isEmpty() && !rangeEnd.isBlank()) {
            predicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER)));
        }
        Pageable page = PageRequest.of(from / size, size);

        return eventRepository.findAll(predicate, page).stream()
                .map(EventMapper::toFullDto).toList();
    }

    @Override
    public EventFullDto updateAdminEvent(UpdateEventUserRequest updateEventAdminRequest, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("You can`t change this event");
        }
        if (updateEventAdminRequest.getEventDate() != null && LocalDateTime
                .parse(updateEventAdminRequest.getEventDate(), DATE_TIME_FORMATTER)
                .isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("You can`t change this event");
        }

        updateEventFields(event, updateEventAdminRequest);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> eventSearch(String text, List<Long> categories,
                                           Boolean paid, String rangeStart,
                                           String rangeEnd, Boolean onlyAvailable,
                                           String sort, Integer from, Integer size) {

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QEvent.event.state.eq(EventStatus.PUBLISHED));
        if (!categories.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            predicate.and(QEvent.event.paid.eq(paid));
        }
        if (text != null && !text.isEmpty() && !text.isBlank()) {
            predicate.and(QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text)));
        }
        if (rangeStart != null && rangeEnd != null &&
                !rangeStart.isEmpty() && !rangeStart.isBlank() &&
                !rangeEnd.isEmpty() && !rangeEnd.isBlank()) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
            if (start.isBefore(end)) {
                predicate.and(QEvent.event.eventDate.after(start));
                predicate.and(QEvent.event.eventDate.before(end));
            } else {
                throw new ValidationException("rangeStart must be before rangeEnd");
            }
        } else {
            predicate.and((QEvent.event.eventDate.after(LocalDateTime.now())));
        }

        if (onlyAvailable != null && onlyAvailable) {
            predicate.and(QEvent.event.participantLimit.eq(0).or(
                    QEvent.event.participantRequests.size().lt(QEvent.event.participantLimit)
            ));
        }

        Sort.Direction direction = Sort.Direction.DESC;
        Sort eventSort = Sort.by(direction, "id");

        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                eventSort = Sort.by(direction, "eventDate");
            } else if (sort.equals("VIEWS")) {
                eventSort = Sort.by(direction, "views");
            }
        }
        Pageable page = PageRequest.of(from / size, size, eventSort);
        List<Event> prepareResult = eventRepository.findAll(predicate, page).stream().toList();
        setViewsToEvents(prepareResult);
        return prepareResult.stream()
                .map(EventMapper::toShortDto).toList();
    }

    @Override
    public EventFullDto getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Event with id=" + id + " was not found"));
        if (event.getState().equals(EventStatus.PUBLISHED)) {
            setViewsToEvent(event);
            return EventMapper.toFullDto(event);
        }
        throw new NotFoundException("Event with id=" + id + " was not found");
    }

    private void updateEventFields(Event event, UpdateEventUserRequest eventUserRequest) {
        if (eventUserRequest.getAnnotation() != null) {
            event.setAnnotation(eventUserRequest.getAnnotation());
        }
        if (eventUserRequest.getDescription() != null) {
            event.setDescription(eventUserRequest.getDescription());
        }
        if (eventUserRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventUserRequest.getEventDate(), DATE_TIME_FORMATTER));
        }
        if (eventUserRequest.getPaid() != null) {
            event.setPaid(eventUserRequest.getPaid());
        }
        if (eventUserRequest.getParticipantLimit() != null) {
            if (eventUserRequest.getParticipantLimit() < 0) {
                throw new ValidationException("Limit must be more than 0");
            }
            event.setParticipantLimit(eventUserRequest.getParticipantLimit());
        }

        if (eventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventUserRequest.getRequestModeration());
        }
        if (eventUserRequest.getStateAction() != null) {
            if (eventUserRequest.getStateAction().equals(EventState.CANCEL_REVIEW)) {
                event.setState(EventStatus.CANCELED);
            }
            if (eventUserRequest.getStateAction().equals(EventState.SEND_TO_REVIEW)) {
                event.setState(EventStatus.PENDING);
            }
            if (eventUserRequest.getStateAction().equals(EventState.PUBLISH_EVENT)) {
                if (event.getState().equals(EventStatus.CANCELED)) {
                    throw new ConflictException("You cat`t publish canceled Event");
                }
                event.setState(EventStatus.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            if (eventUserRequest.getStateAction().equals(EventState.REJECT_EVENT)) {
                event.setState(EventStatus.CANCELED);
            }
        }
        if (eventUserRequest.getTitle() != null) {
            event.setTitle(eventUserRequest.getTitle());
        }

        if (eventUserRequest.getCategory() != null &&
                !event.getCategory().getId().equals(eventUserRequest.getCategory())) {
            Category category = categoryRepository.findById(eventUserRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=" + eventUserRequest.getCategory() + " was not found")
            );
            event.setCategory(category);
        }

        if (eventUserRequest.getLocation() != null) {
            if (event.getLocation().getLat() != eventUserRequest.getLocation().getLat() ||
                    event.getLocation().getLon() != eventUserRequest.getLocation().getLon()) {
                Location newLoc = Location.builder()
                        .lat(eventUserRequest.getLocation().getLat())
                        .lon(eventUserRequest.getLocation().getLon())
                        .build();
                locationRepository.save(newLoc);
                event.setLocation(newLoc);
            }
        }
        event.setUpdatedOn(LocalDateTime.now());
    }

    private Event checkIsUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        return event;
    }

    private void setViewsToEvent(Event event) {
        LocalDateTime start = event.getCreatedOn();
        try {
            List<StatResponseDto> statResponseDtoList = statClient.getStats(start,
                    LocalDateTime.now(), List.of("/events/" + event.getId()), true);
            if (statResponseDtoList != null && !statResponseDtoList.isEmpty()) {
                event.setViews(statResponseDtoList.getFirst().getHits());
            } else {
                event.setViews(1L);
            }
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());

        }
    }

    private void setViewsToEvents(List<Event> events) {
        LocalDateTime start = events.getFirst().getCreatedOn();
        try {
            List<StatResponseDto> statResponseDtoList = statClient.getStats(start,
                    LocalDateTime.now(), events.stream().map(event -> "/events/" + event.getId()).toList(), true);
            if (statResponseDtoList != null && !statResponseDtoList.isEmpty()) {
                for (Event event : events) {
                    Optional<StatResponseDto> statResponse = statResponseDtoList.stream()
                            .filter(stat -> stat.getUri().equals("/events/" + event.getId())).findFirst();
                    statResponse.ifPresent(statResponseDto -> event.setViews(statResponseDto.getHits()));
                }
            }
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());

        }
    }

    @Override
    public EventAdminCommentDto addAdminCommentAndChangeStatus(NewEventAdminCommentDto eventAdminComment) {
        Event event = eventRepository.findById(eventAdminComment.getEventId()).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventAdminComment.getEventId() + " was not found")
        );
        if (!event.getState().equals(EventStatus.PENDING)) {
            throw new ValidationException("You can add comment just for events with status Pending");
        }

        event.setState(EventStatus.NEED_CORRECTIONS);
        eventRepository.save(event);
        AdminComment newAdminComment = AdminComment.builder()
                .event(event)
                .text(eventAdminComment.getText())
                .status(AdminCommentStatus.ACTUAL)
                .createdAt(LocalDateTime.now())
                .build();

        return AdminCommentMapper.toAdminCommentDto(adminCommentRepository.save(newAdminComment));
    }

    @Override
    public List<EventAdminCommentDto> getAdminComment(Long userId, Long eventId) {
        Event event = checkIsUserEvent(userId, eventId);
        if(!event.getState().equals(EventStatus.NEED_CORRECTIONS)) {
            throw new ValidationException("Event doesn't have comments");
        }
        List<AdminComment> eventComments = adminCommentRepository.findAllByEventId(eventId);
        return eventComments.stream().map(AdminCommentMapper::toAdminCommentDto).toList();
    }

    @Override
    public EventFullDto resendEvent(Long userId, Long eventId) {
        Event event = checkIsUserEvent(userId, eventId);
        if(!event.getState().equals(EventStatus.NEED_CORRECTIONS)) {
            throw new ValidationException("You can resend to moderation events on status 'Need correction'");
        }
        AdminComment actualComment = adminCommentRepository.findByEventIdAndStatus(eventId, AdminCommentStatus.ACTUAL);
        if(actualComment == null) {
            throw new NotFoundException("Admin comment for event with id=" + eventId + " was not found");
        }
        if(actualComment.getCreatedAt().isAfter(event.getUpdatedOn())) {
            throw new ConflictException("You should fix Event before resend to moderation");
        }
        actualComment.setStatus(AdminCommentStatus.OUTDATED);
        adminCommentRepository.save(actualComment);

        event.setState(EventStatus.PENDING);
        return EventMapper.toFullDto(eventRepository.save(event));
    }


}
