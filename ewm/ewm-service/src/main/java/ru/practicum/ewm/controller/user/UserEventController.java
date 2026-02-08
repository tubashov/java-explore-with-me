package ru.practicum.ewm.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.ParticipationRequestService;

@RestController
@RequiredArgsConstructor
public class UserEventController {

    private final ParticipationRequestService requestService;

}
