package com.example.tinkerbell.todo.Dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoQuery {
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate from;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate to;
}
