package com.example.tinkerbell.todo.Dto;

import lombok.Getter;
import lombok.Setter;

public class TodoDto {

	@Setter
	@Getter
	public static class Request {
		private String title;
	}

	@Setter
	@Getter
	public static class IsCompletedRequest {
		private boolean isCompleted;
	}

	@Setter
	@Getter
	public static class Response {
		private int id;
		private String title;
		private boolean isCompleted;
	}
}
