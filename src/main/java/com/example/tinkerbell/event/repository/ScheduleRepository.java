package com.example.tinkerbell.event.repository;

import com.example.tinkerbell.event.entity.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
}
