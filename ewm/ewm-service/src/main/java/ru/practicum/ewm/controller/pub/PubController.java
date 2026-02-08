package ru.practicum.ewm.controller.pub;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PubController {

    @GetMapping("/pub/health")
    public String health() {
        return "Public API is working";
    }
}
