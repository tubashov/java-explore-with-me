package ru.practicum.ewm.dto.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRatingDto {

    private Long eventId;

    private Long userId;

    private Boolean liked; // true = лайк, false = дизлайк
}
