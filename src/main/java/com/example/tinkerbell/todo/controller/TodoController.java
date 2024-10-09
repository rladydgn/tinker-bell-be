package com.example.tinkerbell.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.todo.Dto.TodoDto;
import com.example.tinkerbell.todo.service.TodoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todos")
public class TodoController {
	private final TodoService todoService;

	@Operation(summary = "유저의 todo 단건 조회")
	@GetMapping(value = "/{id}")
	public ResponseEntity<TodoDto.Response> getTodo(@PathVariable int id, @Parameter(hidden = true) @Login User user) {
		return ResponseEntity.ok(todoService.getTodo(id, user));
	}

	@Operation(summary = "유저의 todo 목록 조회")
	@GetMapping
	public ResponseEntity<List<TodoDto.Response>> getTodoList(@Parameter(hidden = true) @Login User user) {
		return ResponseEntity.ok(todoService.getTodoList(user));
	}

	@Operation(summary = "todo 생성")
	@PostMapping
	public ResponseEntity<Void> saveTodo(@RequestBody TodoDto.Request todoDto,
		@Parameter(hidden = true) @Login User user) {
		todoService.saveTodo(todoDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "todo 수정", description = "유저 본인이 생성한 todo 만 수정 가능")
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> changeTodo(@PathVariable int id,
		@RequestBody TodoDto.Request todoDto,
		@Parameter(hidden = true) @Login User user) {
		todoService.changeTodo(id, todoDto, user);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "todo 삭제", description = "유저 본인이 생성한 todo 만 삭제 가능")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> removeTodo(@PathVariable int id, @Parameter(hidden = true) @Login User user) {
		todoService.removeTodo(id, user);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "todo 완료 상태 변경")
	@PatchMapping(value = "completion/{id}")
	public ResponseEntity<Void> changeTodoIsCompleted(@PathVariable int id,
		@RequestBody TodoDto.IsCompletedRequest todoIsCompletedDto, @Parameter(hidden = true) @Login User user) {
		todoService.changeTodoIsCompleted(id, todoIsCompletedDto, user);
		return ResponseEntity.ok().build();
	}
}
