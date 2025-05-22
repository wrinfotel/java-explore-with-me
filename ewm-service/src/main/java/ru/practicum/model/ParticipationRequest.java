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

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    private RequestStatus status;

    private LocalDateTime created;
}
