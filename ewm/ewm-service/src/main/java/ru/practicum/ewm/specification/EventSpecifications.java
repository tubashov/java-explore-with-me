package ru.practicum.ewm.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.dto.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> isPublished() {
        return (root, query, cb) ->
                cb.equal(root.get("state"), EventState.PUBLISHED);
    }

    public static Specification<Event> textLike(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) return null;

            String pattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Event> categoryIn(List<Long> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) return null;
            return root.get("category").get("id").in(categories);
        };
    }

    public static Specification<Event> paid(Boolean paid) {
        return (root, query, cb) ->
                paid == null ? null : cb.equal(root.get("paid"), paid);
    }

    public static Specification<Event> dateFrom(LocalDateTime start) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("eventDate"), start);
    }

    public static Specification<Event> dateTo(LocalDateTime end) {
        return (root, query, cb) ->
                end == null ? null :
                        cb.lessThanOrEqualTo(root.get("eventDate"), end);
    }

    public static Specification<Event> onlyAvailable(Boolean onlyAvailable) {
        return (root, query, cb) -> {
            if (!Boolean.TRUE.equals(onlyAvailable)) return null;

            return cb.or(
                    cb.equal(root.get("participantLimit"), 0),
                    cb.lessThan(
                            root.get("confirmedRequests"),
                            root.get("participantLimit")
                    )
            );
        };
    }
}
