package com.example.tinkerbell.event.repository;

import com.example.tinkerbell.event.entity.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
	List<Event> findAllByUserIdOrderByCreatedAt(int userId);
}
