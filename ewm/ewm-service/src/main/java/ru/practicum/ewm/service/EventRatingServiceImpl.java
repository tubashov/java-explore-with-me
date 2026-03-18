package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.EventRatingDto;
import ru.practicum.ewm.mapper.EventRatingMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventRating;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRatingRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventRatingServiceImpl implements EventRatingService {

    private final EventRatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventRatingDto setRating(Long eventId, Long userId, Boolean liked) {
        log.info("Setting rating for eventId={} by userId={} liked={}", eventId, userId, liked);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found: id={}", eventId);
                    return new IllegalArgumentException("Event not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: id={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        Optional<EventRating> existingRatingOpt = ratingRepository.findByEventIdAndUserId(eventId, userId);

        EventRating rating;
        if (existingRatingOpt.isPresent()) {
            rating = existingRatingOpt.get();
            rating.setLiked(liked);
            log.info("Updated existing rating id={} to liked={}", rating.getId(), liked);
        } else {
            rating = EventRatingMapper.toEntity(
                    EventRatingDto.builder().liked(liked).build(),
                    event,
                    user
            );
            log.info("Created new rating for eventId={} userId={}", eventId, userId);
        }

        EventRating saved = ratingRepository.save(rating);
        log.info("Rating saved: id={}, eventId={}, userId={}, liked={}",
                saved.getId(), saved.getEvent().getId(), saved.getUser().getId(), saved.getLiked());

        return EventRatingMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getRating(Long eventId) {
        log.info("Getting rating for eventId={}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found: id={}", eventId);
                    return new IllegalArgumentException("Event not found");
                });

        long likes = ratingRepository.countByEventIdAndLiked(eventId, true);
        long dislikes = ratingRepository.countByEventIdAndLiked(eventId, false);

        Map<String, Long> result = new HashMap<>();
        result.put("likes", likes);
        result.put("dislikes", dislikes);

        log.info("Rating for eventId={}: likes={}, dislikes={}", eventId, likes, dislikes);

        return result;
    }
}
