package ru.practicum.ewm.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@Slf4j  // Добавляем аннотацию для логирования
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@Validated
public class PrivateEventRequestController {

    private final ParticipationRequestService requestService;

    // 10 Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping
    public List<ParticipationRequestDto> getEventRequests(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId
    ) {
        log.info("User with id={} is requesting participation requests for event with id={}.", userId, eventId);
        List<ParticipationRequestDto> requests = requestService.getEventRequests(userId, eventId);

        log.info("Found {} participation requests for event with id={}.", requests.size(), eventId);
        return requests;
    }

    // 11 Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping
    public EventRequestStatusUpdateResult updatePrivateEventRequests(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest request
    ) {
        log.info("User with id={} is updating participation requests for event with id={}. Status update request: {}",
                userId, eventId, request);
        EventRequestStatusUpdateResult result = requestService.updateRequests(userId, eventId, request);

        log.info("Updated participation requests for event with id={} with result: {}", eventId, result);
        return result;
    }
}
