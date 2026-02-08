package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.dto.event.EventState;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @Column(nullable = false, length = 7000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Embedded
    private EventLocation location;

    @Column(nullable = false)
    private Boolean paid;

    private Integer participantLimit;

    @Column(nullable = false)
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    @Builder.Default
    @Column(nullable = false)
    private Long confirmedRequests = 0L;

    @Builder.Default
    @Column(nullable = false)
    private Long views = 0L;
}
