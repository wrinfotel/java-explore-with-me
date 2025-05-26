package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, page).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .toList();
        }
        return compilationRepository.findAll(page).stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();

    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id=" + compId + " was not found")
        );
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilation) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilation);
        if (newCompilation.getEvents() != null && !newCompilation.getEvents().isEmpty()) {
            Set<Event> eventsSet = new HashSet<>();
            for (Long eventId : newCompilation.getEvents()) {
                Event event = eventRepository.findById(eventId).orElseThrow(
                        () -> new NotFoundException("Event with id=" + eventId + " was not found")
                );
                eventsSet.add(event);
            }
            compilation.setEvents(eventsSet);
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id=" + compId + " was not found")
        );
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id=" + compId + " was not found")
        );
        compilation.setPinned(compilationRequest.getPinned() != null ? compilationRequest.getPinned() : false);
        if (compilationRequest.getTitle() != null) {
            compilation.setTitle(compilationRequest.getTitle());
        }
        if (compilationRequest.getEvents() != null && !compilationRequest.getEvents().isEmpty()) {
            Set<Event> eventsSet = new HashSet<>();
            for (Long eventId : compilationRequest.getEvents()) {
                Event event = eventRepository.findById(eventId).orElseThrow(
                        () -> new NotFoundException("Event with id=" + eventId + " was not found")
                );
                eventsSet.add(event);
            }
            compilation.setEvents(eventsSet);
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }
}
