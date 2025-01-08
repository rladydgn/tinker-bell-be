package com.example.tinkerbell.todo.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class CategoryDto {
	@Setter
	@Getter
	@Schema(name = "CategoryResponse")
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
