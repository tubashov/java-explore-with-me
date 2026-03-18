package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.EventRating;

import java.util.Optional;

public interface EventRatingRepository extends JpaRepository<EventRating, Long> {

    // Найти рейтинг конкретного пользователя для конкретного события
    Optional<EventRating> findByEventIdAndUserId(Long eventId, Long userId);

    // Посчитать количество лайков или дизлайков для события
    long countByEventIdAndLiked(Long eventId, Boolean liked);
}
