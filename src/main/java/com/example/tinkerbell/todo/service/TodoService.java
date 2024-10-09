package com.example.tinkerbell.todo.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.todo.Dto.TodoDto;
import com.example.tinkerbell.todo.entity.Todo;
import com.example.tinkerbell.todo.repository.TodoRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TodoService {
	private final TodoRepository todoRepository;
	private final ModelMapper modelMapper;

	@Transactional(readOnly = true)
	public List<TodoDto.Response> getTodoList(User user) {
		List<Todo> todoList = todoRepository.findAllByUserId(user.getId());
		return todoList.stream().map(todo -> modelMapper.map(todo, TodoDto.Response.class)).toList();
	}

	@Transactional(readOnly = true)
	public TodoDto.Response getTodo(int id, User user) {
		Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
			.orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));
		return modelMapper.map(todo, TodoDto.Response.class);
	}

	@Transactional
	public void saveTodo(TodoDto.Request todoDto, User user) {
		Todo todo = modelMapper.map(todoDto, Todo.class);
		todo.setUser(user);
		todoRepository.save(todo);
	}

	@Transactional
	public void changeTodo(int id, TodoDto.Request todoDto, User user) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

		if (!isTodoOwner(todo, user)) {
			throw new ValidationException("todo 의 소유자가 아닙니다.");
		}

		todo.setTitle(todoDto.getTitle());
		todoRepository.save(todo);
	}

	@Transactional
	public void removeTodo(int id, User user) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

		if (!isTodoOwner(todo, user)) {
			throw new ValidationException("todo 의 소유자가 아닙니다.");
		}

		todoRepository.delete(todo);
	}

	private boolean isTodoOwner(Todo todo, User user) {
		return todo.getUser().getId() == user.getId() ? true : false;
	}
}
