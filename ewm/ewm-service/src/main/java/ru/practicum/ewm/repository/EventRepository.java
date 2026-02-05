package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.dto.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    // 4 Удаление категории
    boolean existsByCategoryId(Long categoryId);

    // 6 Получение событий, добавленных текущим пользователем
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    // 8 Получение полной информации о событии добавленном текущим пользователем
    Optional<Event> findByIdAndInitiatorId(Long userId, Long eventId);

    // 14 Поиск событий
    @Query("""
            SELECT e FROM Event e
            WHERE (:useUsers = false OR e.initiator.id IN :users)
              AND (:useStates = false OR e.state IN :states)
              AND (:useCategories = false OR e.category.id IN :categories)
              AND e.eventDate >= :rangeStart
              AND e.eventDate <= :rangeEnd
            """)
    Page<Event> findAdminEvents(
            @Param("users") List<Long> users,
            @Param("useUsers") boolean useUsers,

            @Param("states") List<EventState> states,
            @Param("useStates") boolean useStates,

            @Param("categories") List<Long> categories,
            @Param("useCategories") boolean useCategories,

            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,

            Pageable pageable
    );

    // 17 Получение подробной информации об опубликованном событии по его идентификатору
    Optional<Event> findByIdAndState(Long id, EventState state);

    // 16 Получение событий с возможностью фильтрации
    @Query("""
            SELECT e FROM Event e
            WHERE e.state = :state
              AND (
                   LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
              )
              AND (:categoryIds IS NULL OR e.category.id IN :categoryIds)
              AND (:paid IS NULL OR e.paid = :paid)
              AND e.eventDate BETWEEN :rangeStart AND :rangeEnd
            """)
    Page<Event> searchPublicWithText(
            @Param("text") String text,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("state") EventState state,
            Pageable pageable
    );

    @Query("""
            SELECT e FROM Event e
            WHERE e.state = :state
              AND (:categoryIds IS NULL OR e.category.id IN :categoryIds)
              AND (:paid IS NULL OR e.paid = :paid)
              AND e.eventDate BETWEEN :rangeStart AND :rangeEnd
            """)
    Page<Event> searchPublicWithoutText(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("state") EventState state,
            Pageable pageable
    );
}
