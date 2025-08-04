package ru.practicum.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByCategoryId(Long catId);

    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "participantRequests",
                    "category",
                    "initiator"
            }
    )
    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "participantRequests",
                    "category",
                    "initiator",
                    "location"
            }
    )
    Page<Event> findAll(Predicate predicate, Pageable pageable);
}
