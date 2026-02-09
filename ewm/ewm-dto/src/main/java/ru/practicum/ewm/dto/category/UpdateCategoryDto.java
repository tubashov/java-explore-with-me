package ru.practicum.ewm.dto.category;

import jakarta.validation.constraints.Size;
import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryDto {

    @NotBlank
    @Size(max = 50)
    private String name;
}
