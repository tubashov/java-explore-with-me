package ru.practicum.ewm.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin/health")
    public String health() {
        return "Admin API is working";
    }
}
