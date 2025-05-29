package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.AdminCommentStatus;
import ru.practicum.model.AdminComment;

import java.util.List;

public interface AdminCommentRepository extends JpaRepository<AdminComment, Long> {
    List<AdminComment> findAllByEventId(Long eventId);

    AdminComment findByEventIdAndStatus(Long eventId, AdminCommentStatus adminCommentStatus);
}
