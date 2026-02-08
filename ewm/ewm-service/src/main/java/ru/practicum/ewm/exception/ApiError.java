package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiError {

    private String status;      // BAD_REQUEST, NOT_FOUND, CONFLICT
    private String reason;      // Incorrectly made request.
    private String message;     // Field: title. Error: must not be blank. Value: null

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
