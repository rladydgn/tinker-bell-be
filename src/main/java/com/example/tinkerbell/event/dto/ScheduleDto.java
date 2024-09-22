package com.example.tinkerbell.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

import com.example.tinkerbell.event.entity.Schedule;

public class ScheduleDto {
	@Data
	@ToString
	public static class Request {
		@Min(value = 1)
		@Max(value = 3000)
		private int applicantLimit;
		private LocalDateTime date;
	}

	@Data
	@Builder
	public static class Response {
		private int id;
		private int applicantLimit;
		private int applicantCount;
		private LocalDateTime date;
	}

	public static ScheduleDto.Response toResponse(Schedule schedule) {
		return ScheduleDto.Response.builder()
			.id(schedule.getId())
			.applicantLimit(schedule.getApplicantLimit())
			.applicantCount(schedule.getApplicantCount())
			.date(schedule.getDate())
			.build();
	}
}



