package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.EventRatingDto;

import java.util.Map;

public interface EventRatingService {

    // Поставить или обновить рейтинг
    EventRatingDto setRating(Long eventId, Long userId, Boolean liked);

    // Получить количество лайков и дизлайков для события
    Map<String, Long> getRating(Long eventId);
}
