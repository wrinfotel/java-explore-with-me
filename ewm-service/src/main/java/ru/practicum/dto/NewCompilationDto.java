package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NewCompilationDto {

    private List<Long> events;
    private boolean pinned;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 50)
    private String title;
}
