package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import ru.practicum.ewm.dto.Location;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {
    private String title;

    private String annotation;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private Long categoryId;

    private UserEventStateAction stateAction;
}
