package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventRatingDto;
import ru.practicum.ewm.service.EventRatingService;

import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventRatingController {

    private final EventRatingService ratingService;

    // Поставить или обновить рейтинг
    @PostMapping("/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingDto rateEvent(
            @PathVariable Long eventId,
            @RequestParam Long userId,
            @RequestParam Boolean liked) {
        return ratingService.setRating(eventId, userId, liked);
    }

    // Получить рейтинг события
    @GetMapping("/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Long> getEventRating(@PathVariable Long eventId) {
        return ratingService.getRating(eventId);
    }
}
