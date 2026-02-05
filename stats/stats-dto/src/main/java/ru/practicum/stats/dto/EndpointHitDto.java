package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHitDto {

    private Long id;

    @NotBlank(message = "App cannot be blank")
    private String app;

    @NotBlank(message = "URI cannot be blank")
    private String uri;

    @NotBlank(message = "IP cannot be blank")
    @Pattern(
            // Полный regex для IPv4 и IPv6 (включая сокращенные IPv6)
            regexp = "^((" +
                    // IPv4
                    "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}" +
                    "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)" +
                    ")" +
                    "|" +
                    // Полный IPv6
                    "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,7}:|" +
                    "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
                    "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
                    "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
                    ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
                    "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
                    "::(ffff(:0{1,4}){0,1}:){0,1}" +
                    "((25[0-5]|(2[0-4]|1{0,1}\\d|)[0-9])\\.){3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}\\d|)[0-9])|" +
                    "([0-9a-fA-F]{1,4}:){1,4}:" +
                    "((25[0-5]|(2[0-4]|1{0,1}\\d|)[0-9])\\.){3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}\\d|)[0-9])" +
                    "))$",
            message = "Invalid IP address"
    )
    private String ip;

    @NotNull(message = "Timestamp cannot be null")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
