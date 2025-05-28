package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.dto.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "participation_requests")
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime created;
}
