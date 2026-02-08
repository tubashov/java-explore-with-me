package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.EventState;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.RequestStatus;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // 10 Получение информации о запросах на участие в событии текущего пользователя
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User is not initiator");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .toList();
    }

    // 11 Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest dto
    ) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User is not initiator of the event");
        }

        if (!event.getRequestModeration()) {
            throw new ConflictException("Request moderation is disabled");
        }

        List<ParticipationRequest> requests =
                requestRepository.findAllByIdIn(dto.getRequestIds());

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        for (ParticipationRequest r : requests) {

            if (!r.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Request does not belong to this event");
            }

            if (r.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must be PENDING");
            }

            if (dto.getStatus() == RequestStatus.CONFIRMED) {

                if (event.getParticipantLimit() != 0 &&
                        event.getConfirmedRequests() >= event.getParticipantLimit()) {

                    throw new ConflictException("Participant limit reached");
                }

                r.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmed.add(ParticipationRequestMapper.toDto(r));

            } else {
                r.setStatus(RequestStatus.REJECTED);
                rejected.add(ParticipationRequestMapper.toDto(r));
            }
        }

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    // 18 Получение информации о заявках текущего пользователя на участие в чужих событиях
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::toDto)
                .toList();
    }

    // 19 Добавление запроса от текущего пользователя на участие в событии
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation");
        }

        if (requestRepository
                .existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getParticipantLimit() > 0 &&
                event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
        }

        RequestStatus status;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            status = RequestStatus.PENDING;
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .status(status)
                .created(LocalDateTime.now())
                .build();

        return ParticipationRequestMapper.toDto(
                requestRepository.save(request)
        );
    }

    // 20 Отмена своего запроса на участие в событии
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        ParticipationRequest request = requestRepository
                .findByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Request with id=" + requestId + " was not found"));

        if (request.getStatus() == RequestStatus.CANCELED) {
            return ParticipationRequestMapper.toDto(request);
        }

        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new ConflictException("Request already rejected");
        }

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(
                    Math.max(0, event.getConfirmedRequests() - 1)
            );
        }

        request.setStatus(RequestStatus.CANCELED);

        return ParticipationRequestMapper.toDto(request);
    }

}
