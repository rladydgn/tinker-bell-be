package com.example.tinkerbell.todo.Dto;

import lombok.Getter;
import lombok.Setter;

public class CategoryDto {
	@Setter
	@Getter
	public static class Request {
		private String name;
		private String color;
	}

	@Setter
	@Getter
	public static class Response {
		private int id;
		private String name;
		private String color;
	}
}
