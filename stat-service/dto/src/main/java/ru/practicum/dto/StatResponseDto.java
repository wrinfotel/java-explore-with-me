package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatResponseDto {
    private String app;
    private String uri;
    private Long hits;
}
