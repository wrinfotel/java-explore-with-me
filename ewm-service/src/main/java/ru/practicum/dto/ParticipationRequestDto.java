package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipationRequestDto {

    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private RequestStatus status;

}
