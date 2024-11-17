package com.example.tinkerbell.todo.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class TodoDto {

	@Setter
	@Getter
	public static class Request {
		private String title;
		private LocalDateTime date;
		private Integer order;
	}

	@Setter
	@Getter
	public static class Order {
		private int id;
		private int order;
	}

	@Setter
	@Getter
	public static class OrderRequest {
		private List<Order> orderList;
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
		private Integer order;
	}

	@Setter
	@Getter
	public static class Query {
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		private LocalDate from;
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		private LocalDate to;
	}
}
