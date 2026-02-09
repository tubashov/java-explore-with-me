package ru.practicum.ewm.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@RequiredArgsConstructor
public class DbCleaner {

    private final JdbcTemplate jdbc;

    @PostConstruct
    public void clean() {
        jdbc.execute("""
            TRUNCATE TABLE
                categories,
                users,
                events,
                compilations,
                participation_requests
            RESTART IDENTITY CASCADE
        """);
    }
}

