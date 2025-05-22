package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEventAdminRequest {

    @NotNull
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotNull
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @NotBlank
    private String eventDate;

    @NotNull
    private Location location;

    private boolean paid;

    private Integer participantLimit;

    private boolean requestModeration;

    private EventState stateAction;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
