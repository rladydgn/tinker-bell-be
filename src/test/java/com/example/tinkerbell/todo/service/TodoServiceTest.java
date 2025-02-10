package com.example.tinkerbell.todo.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.todo.Dto.TodoRequestDto;
import com.example.tinkerbell.todo.Dto.TodoResponseDto;
import com.example.tinkerbell.todo.entity.Todo;
import com.example.tinkerbell.todo.repository.CategoryRepository;
import com.example.tinkerbell.todo.repository.TodoRepository;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoServiceTest {
	User user;
	private TodoService todoService;
	TodoRepository todoRepository = mock(TodoRepository.class);
	CategoryRepository categoryRepository = mock(CategoryRepository.class);

	@BeforeAll
	void init() {
		this.todoService = new TodoService(todoRepository, categoryRepository);

		this.user = User.builder()
			.id(1)
			.nickname("test")
			.email("test@test.com")
			.provider("test")
			.authId("test")
			.build();

		System.out.println(this.user);
	}

	@Test
	void TodoSave() {
		// given
		TodoRequestDto todoRequestDto = new TodoRequestDto();

		String title = "test title";
		String description = "test description";
		LocalDateTime date = LocalDateTime.of(2025, 2, 1, 0, 0);
		List<Integer> categoryIdList = new ArrayList<>();

		todoRequestDto.setTitle(title);
		todoRequestDto.setDescription(description);
		todoRequestDto.setDate(date);
		todoRequestDto.setCategoryIdList(categoryIdList);

		Todo todo = new Todo();
		todo.setId(1);
		todo.setTitle(title);
		todo.setDescription(description);
		todo.setDate(date);
		todo.setUser(user);
		todo.setOrder(0);
		todo.setCompleted(false);

		// when
		when(todoRepository.save(any())).thenReturn(todo);
		TodoResponseDto todoResponseDto = todoService.saveTodo(todoRequestDto, user);

		// then
		Assertions.assertThat(todoResponseDto.getId()).isEqualTo(1);
		Assertions.assertThat(todoResponseDto.getTitle()).isEqualTo(title);
		Assertions.assertThat(todoResponseDto.getDescription()).isEqualTo(description);
		Assertions.assertThat(todoResponseDto.isCompleted()).isFalse();
	}
}
