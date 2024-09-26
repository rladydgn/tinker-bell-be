package com.example.tinkerbell.event.repository;

import java.util.List;

import com.example.tinkerbell.event.entity.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	List<Schedule> findByEventId(int eventId);
}
