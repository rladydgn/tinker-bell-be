package com.example.tinkerbell.todo.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
	public TodoDto.ListResponse getTodoList(User user, TodoDto.Query todoQuery) {
		LocalDateTime from = todoQuery.getFrom().atStartOfDay();
		LocalDateTime to = todoQuery.getTo().atTime(LocalTime.of(23, 59, 59));
		System.out.println(from);
		System.out.println(to);

		List<Todo> completedTodoList = todoRepository.findAllByUserIdAndIsCompletedAndDateBetweenOrderByOrderAsc(
			user.getId(), true,
			from, to);
		List<Todo> incompletedTodoList = todoRepository.findAllByUserIdAndIsCompletedAndDateBetweenOrderByOrderAsc(
			user.getId(), false, from, to);

		TodoDto.ListResponse listResponse = new TodoDto.ListResponse();
		listResponse.setCompletedTodoList(
			completedTodoList.stream().map(todo -> modelMapper.map(todo, TodoDto.Response.class)).toList());
		listResponse.setIncompletedTodoList(
			incompletedTodoList.stream().map(todo -> modelMapper.map(todo, TodoDto.Response.class)).toList());
		return listResponse;
	}

	@Transactional(readOnly = true)
	public TodoDto.Response getTodo(int id, User user) {
		Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
			.orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));
		return modelMapper.map(todo, TodoDto.Response.class);
	}

	@Transactional
	public TodoDto.Response saveTodo(TodoDto.Request todoDto, User user) {
		Todo todo = modelMapper.map(todoDto, Todo.class);
		todo.setUser(user);

		LocalDateTime from = todoDto.getDate().toLocalDate().atStartOfDay();
		LocalDateTime to = todoDto.getDate().toLocalDate().atTime(LocalTime.of(23, 59, 59));
		Optional<Todo> maxOrderTodo = todoRepository.findFirstByUserIdAndIsCompletedAndDateBetweenOrderByOrderDesc(
			user.getId(), false, from, to);

		if (maxOrderTodo.isEmpty()) {
			System.out.println("empty");
			todo.setOrder(0);
		} else {
			System.out.println(maxOrderTodo.get());
			todo.setOrder(maxOrderTodo.get().getOrder() + 1);
		}
		return modelMapper.map(todoRepository.save(todo), TodoDto.Response.class);
	}

	@Transactional
	public void changeTodo(int id, TodoDto.Request todoDto, User user) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

		if (!isTodoOwner(todo, user)) {
			throw new ValidationException("todo 의 소유자가 아닙니다.");
		}

		todo.setTitle(todoDto.getTitle());
		// 날짜 변경시 할일의 순서를 해당 날짜의 제일 마지막으로 수정
		if (todo.getDate() != todoDto.getDate()) {
			LocalDateTime from = todoDto.getDate().toLocalDate().atStartOfDay();
			LocalDateTime to = todoDto.getDate().toLocalDate().atTime(LocalTime.of(23, 59, 59));
			Optional<Todo> maxOrderTodo = todoRepository.findFirstByUserIdAndIsCompletedAndDateBetweenOrderByOrderDesc(
				user.getId(), false, from, to);

			if (maxOrderTodo.isEmpty()) {
				System.out.println("empty");
				todo.setOrder(0);
			} else {
				System.out.println(maxOrderTodo.get());
				todo.setOrder(maxOrderTodo.get().getOrder() + 1);
			}
		}
		todo.setDate(todoDto.getDate());
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

	@Transactional
	public void changeTodoIsCompleted(int id, TodoDto.IsCompletedRequest todoIsCompletedDto, User user) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

		if (!isTodoOwner(todo, user)) {
			throw new ValidationException("todo 의 소유자가 아닙니다.");
		}

		todo.setCompleted(todoIsCompletedDto.isCompleted());
		todoRepository.save(todo);
	}

	@Transactional
	public void changeTodoOrder(TodoDto.OrderRequest orderRequest, User user) {
		orderRequest.getOrderList().stream().forEach(order -> {
			Todo todo = todoRepository.findById(order.getId())
				.orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

			if (!isTodoOwner(todo, user)) {
				throw new ValidationException("todo 의 소유자가 아닙니다. id: " + todo.getId());
			}

			todo.setOrder(order.getOrder());
			todoRepository.save(todo);
		});
	}

	private boolean isTodoOwner(Todo todo, User user) {
		return todo.getUser().getId() == user.getId() ? true : false;
	}
}
