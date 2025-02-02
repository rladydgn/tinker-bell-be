package com.example.tinkerbell.todo.Dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoRequestDto {
	private String title;
	private LocalDateTime date;
	private String description;
	private List<Integer> categoryIdList;
}
