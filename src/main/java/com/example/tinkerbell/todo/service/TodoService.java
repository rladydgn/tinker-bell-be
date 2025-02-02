package com.example.tinkerbell.todo.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.todo.Dto.TodoIsCompletedRequestDto;
import com.example.tinkerbell.todo.Dto.TodoListResponseDto;
import com.example.tinkerbell.todo.Dto.TodoOrderListRequestDto;
import com.example.tinkerbell.todo.Dto.TodoQuery;
import com.example.tinkerbell.todo.Dto.TodoRequestDto;
import com.example.tinkerbell.todo.Dto.TodoResponseDto;
import com.example.tinkerbell.todo.entity.Category;
import com.example.tinkerbell.todo.entity.Todo;
import com.example.tinkerbell.todo.repository.CategoryRepository;
import com.example.tinkerbell.todo.repository.TodoCategoryRepository;
import com.example.tinkerbell.todo.repository.TodoRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TodoService {
	private final TodoRepository todoRepository;
	private final ModelMapper modelMapper;
	private final CategoryRepository categoryRepository;
	private final TodoCategoryRepository todoCategoryRepository;

	@Transactional(readOnly = true)
	public TodoListResponseDto getTodoList(User user, TodoQuery todoQuery) {
		LocalDateTime from = todoQuery.getFrom().atStartOfDay();
		LocalDateTime to = todoQuery.getTo().atTime(LocalTime.of(23, 59, 59));

		List<Todo> completedTodoList = todoRepository.findAllByUserIdAndIsCompletedAndDateBetweenOrderByOrderAsc(
			user.getId(), true,
			from, to);
		List<Todo> incompletedTodoList = todoRepository.findAllByUserIdAndIsCompletedAndDateBetweenOrderByOrderAsc(
			user.getId(), false, from, to);

		System.out.println(incompletedTodoList);

		TodoListResponseDto listResponse = new TodoListResponseDto();
		listResponse.setCompletedTodoList(completedTodoList.stream().map(TodoResponseDto::toDto).toList());
		listResponse.setIncompletedTodoList(incompletedTodoList.stream().map(TodoResponseDto::toDto).toList());
		return listResponse;
	}

	@Transactional(readOnly = true)
	public TodoResponseDto getTodo(int id, User user) {
		Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
			.orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));
		return TodoResponseDto.toDto(todo);
	}

	@Transactional
	public TodoResponseDto saveTodo(TodoRequestDto todoDto, User user) {
		Todo todo = new Todo();
		todo.setTitle(todoDto.getTitle());
		todo.setDate(todoDto.getDate());
		todo.setDescription(todoDto.getDescription());
		todo.setUser(user);

		// 카테고리 가져오기
		List<Category> categoryList = new ArrayList<>();
		todoDto.getCategoryIdList().stream().forEach(categoryId -> categoryList.add(
			categoryRepository.findByIdAndUserId(categoryId, user.getId())
				.orElseThrow(() -> new ValidationException("찾을 수 없는 카테고리 입니다."))
		));

		todo.setCategoryList(categoryList);

		// 순서 설정
		LocalDateTime from = todoDto.getDate().toLocalDate().atStartOfDay();
		LocalDateTime to = todoDto.getDate().toLocalDate().atTime(LocalTime.of(23, 59, 59));
		Optional<Todo> maxOrderTodo = todoRepository.findFirstByUserIdAndIsCompletedAndDateBetweenOrderByOrderDesc(
			user.getId(), false, from, to);

		if (maxOrderTodo.isEmpty()) {
			todo.setOrder(0);
		} else {
			todo.setOrder(maxOrderTodo.get().getOrder() + 1);
		}

		return TodoResponseDto.toDto(todoRepository.save(todo));
	}

	@Transactional
	public void changeTodo(int id, TodoRequestDto todoDto, User user) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

		if (!isTodoOwner(todo, user)) {
			throw new ValidationException("todo 의 소유자가 아닙니다.");
		}

		todo.setTitle(todoDto.getTitle());
		todo.setDescription(todoDto.getDescription());
		// 날짜 변경시 할일의 순서를 해당 날짜의 제일 마지막으로 수정
		if (!todo.getDate().isEqual(todoDto.getDate())) {
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

		// 카테고리 수정
		List<Category> categoryList = new ArrayList<>();
		todoDto.getCategoryIdList().stream().forEach(categoryId -> categoryList.add(
			categoryRepository.findByIdAndUserId(categoryId, user.getId())
				.orElseThrow(() -> new ValidationException("찾을 수 없는 카테고리 입니다."))
		));
		todo.setCategoryList(categoryList);

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
	public void changeTodoIsCompleted(int id, TodoIsCompletedRequestDto todoIsCompletedDto, User user) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ValidationException("찾을 수 없는 todo 입니다."));

		if (!isTodoOwner(todo, user)) {
			throw new ValidationException("todo 의 소유자가 아닙니다.");
		}

		todo.setCompleted(todoIsCompletedDto.isCompleted());
		todoRepository.save(todo);
	}

	@Transactional
	public void changeTodoOrder(TodoOrderListRequestDto orderListRequestDto, User user) {
		orderListRequestDto.getOrderList().stream().forEach(order -> {
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
		return todo.getUser().getId() == user.getId();
	}
}
