package com.example.tinkerbell.event.controller;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Void> saveEvent(@Valid EventDto.Request eventDtoRequest) {
        this.eventService.saveEvent(eventDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
