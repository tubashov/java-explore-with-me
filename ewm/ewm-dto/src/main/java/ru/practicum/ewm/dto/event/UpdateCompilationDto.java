package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {

    @Size(max = 50, message = "Title length must be at most 50 characters")
    private String title;

    private Boolean pinned;

    private List<Long> events;
}

