package ru.practicum.ewm.dto.event;

import lombok.Data;
import ru.practicum.ewm.dto.Location;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {

    private String title;

    private String annotation;

    private String description;

    private Long category;

    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private AdminStateAction stateAction;
}
