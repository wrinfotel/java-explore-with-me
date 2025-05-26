package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompilationDto {

    @NotNull
    private Long id;
    private List<EventShortDto> events;

    @NotNull
    private Boolean pinned;

    @NotNull
    @NotBlank
    private String title;
}
