package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    @NotBlank
    @Size(max = 50, message = "Title length must be at most 50 characters")
    private String title;

    private Boolean pinned = false;

    private List<Long> events;
}
