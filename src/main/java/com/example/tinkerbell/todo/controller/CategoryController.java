package com.example.tinkerbell.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.todo.Dto.CategoryDto;
import com.example.tinkerbell.todo.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
	private final CategoryService categoryService;

	@Operation(summary = "category 생성")
	@PostMapping
	public ResponseEntity<CategoryDto.Response> saveCategory(@RequestBody CategoryDto.Request categoryDto,
		@Parameter(hidden = true) @Login User user) {
		CategoryDto.Response response = categoryService.saveCategory(categoryDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "category 목록 조회")
	@GetMapping
	public ResponseEntity<List<CategoryDto.Response>> getCategoryList(@Parameter(hidden = true) @Login User user) {
		List<CategoryDto.Response> responseList = categoryService.getCategoryList(user);
		return ResponseEntity.ok(responseList);
	}

	@Operation(summary = "category 단건 조회")
	@GetMapping("/{id}")
	public ResponseEntity<CategoryDto.Response> getCategory(@PathVariable int id,
		@Parameter(hidden = true) @Login User user) {
		CategoryDto.Response response = categoryService.getCategory(id, user);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "category 수정")
	@PutMapping("/{id}")
	public ResponseEntity<CategoryDto.Response> changeCategory(@PathVariable int id,
		@RequestBody CategoryDto.Request categoryDto, @Parameter(hidden = true) @Login User user) {
		CategoryDto.Response response = categoryService.changeCategory(id, user, categoryDto);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "category 삭제")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeCategory(@PathVariable int id, @Parameter(hidden = true) @Login User user) {
		categoryService.removeCategory(id, user);
		return ResponseEntity.ok().build();
	}
}
