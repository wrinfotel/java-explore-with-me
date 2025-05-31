package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventStatus;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        ParticipationRequest request = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        Integer requestsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getInitiator().getId().equals(userId) ||
                !event.getState().equals(EventStatus.PUBLISHED) ||
                request != null ||
                (event.getParticipantLimit() != 0
                        && event.getParticipantLimit() <= requestsCount)) {
            throw new ConflictException("Could not execute statement; SQL [n/a]; constraint [uq_request]; nested" +
                    " exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + eventId + " was not found"));
        ParticipationRequest newRequest = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .status((event.getParticipantLimit() == 0 ||
                        !event.isRequestModeration()) ? RequestStatus.CONFIRMED : RequestStatus.PENDING)
                .created(LocalDateTime.now())
                .build();
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request with id=" + requestId + " was not found");
        }
        request.setStatus(RequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
