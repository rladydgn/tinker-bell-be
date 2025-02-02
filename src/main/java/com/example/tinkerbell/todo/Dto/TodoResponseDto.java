package com.example.tinkerbell.todo.Dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.tinkerbell.todo.entity.Category;
import com.example.tinkerbell.todo.entity.Todo;
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
	private List<Integer> categoryIdList = new ArrayList<>();

	public static TodoResponseDto toDto(Todo todo) {
		TodoResponseDto todoResponseDto = new TodoResponseDto();
		todoResponseDto.setId(todo.getId());
		todoResponseDto.setTitle(todo.getTitle());
		todoResponseDto.setCompleted(todo.isCompleted());
		todoResponseDto.setDate(todo.getDate());
		todoResponseDto.setOrder(todo.getOrder());
		todoResponseDto.setDescription(todo.getDescription());
		todoResponseDto.setCategoryIdList(todo.getCategoryList().stream().map(Category::getId).toList());

		return todoResponseDto;
	}
}
