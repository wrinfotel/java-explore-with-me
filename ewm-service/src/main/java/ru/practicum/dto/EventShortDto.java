package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventShortDto {

    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    @NotNull
    @NotBlank
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    private EventStatus state;

    private boolean paid;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    private Long views;
}
