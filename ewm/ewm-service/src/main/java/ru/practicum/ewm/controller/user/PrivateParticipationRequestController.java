package ru.practicum.ewm.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
public class PrivateParticipationRequestController {

    private final ParticipationRequestService requestService;

    // 18 Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(
            @Positive @PathVariable Long userId
    ) {
        log.info("User with id={} is requesting their participation requests.", userId);
        List<ParticipationRequestDto> requests = requestService.getUserRequests(userId);

        log.info("User with id={} has {} participation requests.", userId, requests.size());
        return requests;
    }

    // 19 Добавление запроса от текущего пользователя на участие в событии
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @Positive @PathVariable Long userId,
            @Positive @RequestParam Long eventId
    ) {
        log.info("User with id={} is creating a participation request for event with id={}.", userId, eventId);
        ParticipationRequestDto request = requestService.createRequest(userId, eventId);

        log.info("Participation request created for user with id={} and event with id={}. Request ID: {}",
                userId, eventId, request.getId());
        return request;
    }

    // 20 Отмена своего запроса на участие в событии
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @Positive @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        log.info("User with id={} is canceling participation request with id={}.", userId, requestId);
        ParticipationRequestDto canceledRequest = requestService.cancelRequest(userId, requestId);

        log.info("Participation request with id={} canceled for user with id={}.", requestId, userId);
        return canceledRequest;
    }
}
