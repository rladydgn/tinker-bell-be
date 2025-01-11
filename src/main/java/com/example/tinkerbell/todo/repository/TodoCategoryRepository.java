package com.example.tinkerbell.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tinkerbell.todo.entity.TodoCategory;

public interface TodoCategoryRepository extends JpaRepository<TodoCategory, Integer> {
	void deleteByCategoryId(int categoryId);
}
