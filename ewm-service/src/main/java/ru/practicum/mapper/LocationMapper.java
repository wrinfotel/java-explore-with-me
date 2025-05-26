package ru.practicum.mapper;

import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
