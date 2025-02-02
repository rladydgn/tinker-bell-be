package com.example.tinkerbell.todo.Dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoResponseDto {
	private int id;
	private String title;
	@JsonProperty("isCompleted")
	private boolean isCompleted;
	private LocalDateTime date;
	private Integer order;
	private String description;
	private List<CategoryDto.Response> categoryList = new ArrayList<>();
}
