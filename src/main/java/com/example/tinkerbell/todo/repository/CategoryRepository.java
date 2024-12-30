package com.example.tinkerbell.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tinkerbell.todo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	List<Category> findAllByUserIdOrderByIdDesc(int userId);

	Optional<Category> findByIdAndUserId(int id, int userId);
}
