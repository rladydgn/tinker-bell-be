package com.example.tinkerbell.todo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.todo.Dto.CategoryRequestDto;
import com.example.tinkerbell.todo.Dto.CategoryResponseDto;
import com.example.tinkerbell.todo.entity.Category;
import com.example.tinkerbell.todo.repository.CategoryRepository;
import com.example.tinkerbell.todo.repository.TodoCategoryRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;
	private final TodoCategoryRepository todoCategoryRepository;

	@Transactional
	public CategoryResponseDto saveCategory(CategoryRequestDto categoryDto, User user) {
		Category category = modelMapper.map(categoryDto, Category.class);
		category.setUser(user);
		return modelMapper.map(categoryRepository.save(category), CategoryResponseDto.class);
	}

	@Transactional(readOnly = true)
	public List<CategoryResponseDto> getCategoryList(User user) {
		List<Category> categoryList = categoryRepository.findAllByUserIdOrderByIdDesc(user.getId());
		return categoryList.stream()
			.map(category -> modelMapper.map(category, CategoryResponseDto.class))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryResponseDto getCategory(int id, User user) {
		Category category = categoryRepository.findByIdAndUserId(id, user.getId())
			.orElseThrow(() -> new ValidationException("찾을 수 없는 카테고리 입니다."));
		return modelMapper.map(category, CategoryResponseDto.class);
	}

	@Transactional
	public CategoryResponseDto changeCategory(int id, User user, CategoryRequestDto categoryDto) {
		Category category = categoryRepository.findByIdAndUserId(id, user.getId())
			.orElseThrow(() -> new ValidationException("찾을 수 없는 카테고리 입니다."));
		category.setName(categoryDto.getName());
		category.setColor(categoryDto.getColor());
		return modelMapper.map(categoryRepository.save(category), CategoryResponseDto.class);
	}

	@Transactional
	public void removeCategory(int id, User user) {
		Category category = categoryRepository.findByIdAndUserId(id, user.getId())
			.orElseThrow(() -> new ValidationException("찾을 수 없는 카테고리 입니다."));
		categoryRepository.delete(category);
		todoCategoryRepository.deleteByCategoryId(category.getId());
	}
}
