package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.dto.AdminCommentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "admin_comments")
@NoArgsConstructor
@AllArgsConstructor
public class AdminComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String text;

    @Enumerated(EnumType.STRING)
    private AdminCommentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
