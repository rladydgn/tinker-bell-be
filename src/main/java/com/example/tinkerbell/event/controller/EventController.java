package com.example.tinkerbell.event.controller;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.service.EventService;
import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/init")
    public ResponseEntity<Void> saveFirstEventAndSchedules(@Login User user, @RequestBody EventDto.InitRequest eventDtoRequest) {
        this.eventService.saveFirstEventAndSchedules(eventDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
