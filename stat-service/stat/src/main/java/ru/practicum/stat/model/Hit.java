package ru.practicum.stat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "hits")
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app;

    private String uri;

    private String ip;

    @Column(name = "created_at")
    private LocalDateTime timestamp;

}
