package com.example.tinkerbell.event.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.event.dto.ScheduleDto;
import com.example.tinkerbell.event.service.ScheduleService;
import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Deprecated
public class ScheduleController {
	private final ScheduleService scheduleService;

	@Operation(summary = "스케줄 생성", description = "스케줄 생성")
	@PostMapping("/events/{eventId}/schedules")
	public ResponseEntity<Void> saveSchedule(@Login User user, @PathVariable int eventId,
		@RequestBody ScheduleDto.Request request) {
		this.scheduleService.saveSchedule(user, request, eventId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "스케줄 수정", description = "스케줄 수정")
	@PutMapping("/events/{eventId}/schedules/{scheduleId}")
	public ResponseEntity<Void> changeSchedule(@Login User user, @PathVariable int eventId,
		@PathVariable int scheduleId, @RequestBody ScheduleDto.Request request) {
		this.scheduleService.changeSchedule(user, request, eventId, scheduleId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "스케줄 삭제", description = "스케줄 삭제")
	@DeleteMapping("/events/{eventId}/schedules/{scheduleId}")
	public ResponseEntity<Void> removeSchedule(@Login User user, @PathVariable int eventId,
		@PathVariable int scheduleId) {
		this.scheduleService.removeSchedule(user, eventId, scheduleId);
		return ResponseEntity.ok().build();
	}
}
