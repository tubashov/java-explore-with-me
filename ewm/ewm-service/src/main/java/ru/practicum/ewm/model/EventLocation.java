package ru.practicum.ewm.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class EventLocation {

    private Double lat;
    private Double lon;
}

