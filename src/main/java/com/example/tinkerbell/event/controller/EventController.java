package com.example.tinkerbell.event.controller;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.service.EventService;
import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/init")
    public ResponseEntity<Void> saveFirstEventAndSchedules(@Login User user, @RequestBody EventDto.InitRequest eventDtoRequest) {
        this.eventService.saveFirstEventAndSchedules(eventDtoRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<EventDto.Response>> getEvents(@Login User user) {
        List<EventDto.Response> responseList = this.eventService.getEvents(user);
        return ResponseEntity.ok().body(responseList);
    }
}
