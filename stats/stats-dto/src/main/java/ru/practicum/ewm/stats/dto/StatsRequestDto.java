package ru.practicum.ewm.stats.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.dto.validator.DateRangeValid;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DateRangeValid
public class StatsRequestDto {

    @NotNull(message = "Start date is required")
    private LocalDateTime start;

    @NotNull(message = "End date is required")
    private LocalDateTime end;

    private Boolean unique;
}
