package com.example.tinkerbell.todo.Dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class TodoDto {

	@Setter
	@Getter
	public static class Request {
		private String title;
		private LocalDateTime date;
	}

	@Setter
	@Getter
	public static class IsCompletedRequest {
		@JsonProperty("isCompleted")
		private boolean isCompleted;
	}

	@Setter
	@Getter
	public static class Response {
		private int id;
		private String title;
		@JsonProperty("isCompleted")
		private boolean isCompleted;
		private LocalDateTime date;
	}
}
