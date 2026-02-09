package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository
        extends JpaRepository<ParticipationRequest, Long> {

    // 10 Получение информации о запросах на участие в событии текущего пользователя
    List<ParticipationRequest> findAllByEventId(Long eventId);

    // 18 Получение информации о заявках текущего пользователя на участие в чужих событиях
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    // 19 Добавление запроса от текущего пользователя на участие в событии
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    // 20 Отмена своего запроса на участие в событии
    Optional<ParticipationRequest> findByIdAndRequester_Id(Long requestId, Long userId);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);
}
