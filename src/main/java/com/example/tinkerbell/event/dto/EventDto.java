package com.example.tinkerbell.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.example.tinkerbell.event.entity.Event;

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
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private List<ScheduleDto.Response> scheduleDtoList;
	}

	public static EventDto.Response toResponse(Event event, LocalDateTime startDate, LocalDateTime endDate, List<ScheduleDto.Response> scheduleDtoList) {
		return EventDto.Response.builder()
			.id(event.getId())
			.title(event.getTitle())
			.startDate(startDate)
			.endDate(endDate)
			.scheduleDtoList(scheduleDtoList)
			.build();
	}
}
