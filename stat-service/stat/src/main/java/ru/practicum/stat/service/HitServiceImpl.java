package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.stat.exception.ValidationException;
import ru.practicum.stat.mapper.HitMapper;
import ru.practicum.stat.model.Hit;
import ru.practicum.stat.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    public HitResponseDto createHit(HitCreateRequestDto hit) {
        Hit newHit = HitMapper.toHit(hit);
        return HitMapper.toHitDto(hitRepository.save(newHit));
    }

    @Override
    public List<StatResponseDto> getStatistics(LocalDateTime start,
                                               LocalDateTime end,
                                               List<String> uris,
                                               Boolean unique) {
        if (start.isBefore(end)) {
            if (unique) {
                if (uris == null || uris.isEmpty()) {
                    return hitRepository.findAllByTimestampBetweenUnique(start, end);
                }
                return hitRepository.findAllByTimestampBetweenUniqueWithUris(start, end, uris);
            } else {
                if (uris == null || uris.isEmpty()) {
                    return hitRepository.findAllByTimestampBetween(start, end);
                }
                return hitRepository.findAllByTimestampBetweenWithUris(start, end, uris);
            }
        } else {
            throw new ValidationException("Date start must be before Date end");
        }
    }
}
