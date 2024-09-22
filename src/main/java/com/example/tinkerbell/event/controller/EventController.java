package com.example.tinkerbell.event.controller;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.dto.ScheduleDto;
import com.example.tinkerbell.event.service.EventService;
import com.example.tinkerbell.event.service.ScheduleService;
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
	private final ScheduleService scheduleService;

	@PostMapping("/init")
	public ResponseEntity<Void> saveFirstEventAndSchedules(@Login User user,
		@RequestBody EventDto.InitRequest eventDtoRequest) {
		this.eventService.saveFirstEventAndSchedules(eventDtoRequest, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<List<EventDto.Response>> getEvents(@Login User user) {
		List<EventDto.Response> responseList = this.eventService.getEvents(user);
		return ResponseEntity.ok().body(responseList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventDto.Response> getEvent(@Login User user, @PathVariable int id) {
		EventDto.Response response = this.eventService.getEvent(id, user);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/{eventId}/schedules/{scheduleId}")
	public ResponseEntity<ScheduleDto.Response> getSchedule(@Login User user, @PathVariable int eventId,
		@PathVariable int scheduleId) {
		ScheduleDto.Response response = scheduleService.getSchedule(user, eventId, scheduleId);
		return ResponseEntity.ok().body(response);
	}
}
