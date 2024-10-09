package com.example.tinkerbell.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tinkerbell.todo.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
	List<Todo> findAllByUserId(int userId);

	Optional<Todo> findByIdAndUserId(int id, int userId);
}
