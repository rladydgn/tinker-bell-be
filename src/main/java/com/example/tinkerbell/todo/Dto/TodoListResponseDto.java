package com.example.tinkerbell.todo.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoListResponseDto {
	private List<TodoResponseDto> completedTodoList;
	private List<TodoResponseDto> incompletedTodoList;
}
