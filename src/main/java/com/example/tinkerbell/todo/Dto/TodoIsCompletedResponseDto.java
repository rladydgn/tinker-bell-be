package com.example.tinkerbell.todo.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoIsCompletedResponseDto {
	@JsonProperty("isCompleted")
	private boolean isCompleted;
}
