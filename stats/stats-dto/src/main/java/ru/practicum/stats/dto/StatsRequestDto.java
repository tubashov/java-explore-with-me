package ru.practicum.stats.dto;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import ru.practicum.stats.dto.validator.DateRangeValid;

import java.time.LocalDateTime;
import java.util.List;


@DateRangeValid
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsRequestDto {

    @NotNull(message = "Start date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "End date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;

    private List<String> uris;
    private Boolean unique = false;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndAfterStart() {
        return start == null || end == null || !end.isBefore(start);
    }
}
