package com.example.tinkerbell.todo.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoOrderListRequestDto {
	private List<TodoOrderDto> orderList;
}
