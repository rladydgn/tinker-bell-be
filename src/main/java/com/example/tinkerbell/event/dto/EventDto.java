package com.example.tinkerbell.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.example.tinkerbell.event.entity.Event;
import com.example.tinkerbell.event.entity.EventStatus;

public class EventDto {
	@Data
	public static class Request {
		@NotBlank
		private String title;
		private int totalApplicantLimit;
	}

	@Data
	public static class InitRequest {
		@NotBlank
		private String title;
		@Min(value = 1)
		@Max(value = 3000)
		private int totalApplicantLimit;
		private List<ScheduleDto.Request> scheduleDtoList;
	}

	@Data
	@Builder
	public static class Response {
		private int id;
		private String title;
		private int totalApplicantLimit;
		private EventStatus status;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private List<ScheduleDto.Response> scheduleDtoList;
	}

	public static EventDto.Response toResponse(Event event, LocalDateTime startDate, LocalDateTime endDate, List<ScheduleDto.Response> scheduleDtoList) {
		EventStatus eventStatus;
		LocalDateTime now = LocalDateTime.now();
		if(now.isBefore(startDate)) {
			eventStatus = EventStatus.STAND_BY;
		} else if(now.isAfter(endDate)) {
			eventStatus = EventStatus.END;
		} else {
			eventStatus = EventStatus.PROGRESS;
		}

		return Response.builder()
			.id(event.getId())
			.title(event.getTitle())
			.startDate(startDate)
			.endDate(endDate)
			.scheduleDtoList(scheduleDtoList)
			.status(eventStatus)
			.build();
	}
}
