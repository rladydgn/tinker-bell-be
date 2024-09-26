package com.example.tinkerbell.event.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tinkerbell.event.dto.ScheduleDto;
import com.example.tinkerbell.event.entity.Event;
import com.example.tinkerbell.event.entity.Schedule;
import com.example.tinkerbell.event.repository.EventRepository;
import com.example.tinkerbell.event.repository.ScheduleRepository;
import com.example.tinkerbell.oAuth.entity.User;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final EventRepository eventRepository;

	@Transactional(readOnly = true)
	public ScheduleDto.Response getSchedule(User user, int eventId, int scheduleId) {
		Event event = eventRepository.findById(eventId).orElseThrow(() -> new ValidationException("존재하지 않는 이벤트 입니다."));
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new ValidationException("찾을 수 없는 스케줄 입니다."));

		if (!isEventContainsSchedule(user, event, schedule)) {
			throw new ValidationException("유저 권한이 없거나, 이벤트에 속한 스케줄이 아닙니다.");
		}

		return ScheduleDto.Response.builder()
			.id(schedule.getId())
			.applicantLimit(schedule.getApplicantLimit())
			.date(schedule.getDate())
			.build();
	}

	@Transactional
	public void saveSchedule(User user, ScheduleDto.Request request, int eventId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new ValidationException("찾을수 없는 이벤트입니다."));

		if (event.getUserId() != user.getId()) {
			throw new ValidationException("이벤트 접근 권한이 없습니다.");
		}

		List<Schedule> scheduleList = scheduleRepository.findByEventId(eventId);
		int totalApplicantCount = scheduleList.stream().mapToInt(Schedule::getApplicantLimit).sum();

		if (event.getTotalApplicantLimit() < totalApplicantCount + request.getApplicantLimit()) {
			throw new ValidationException(
				"최대 참여 가능 수를 넘을 수 없습니다. 현재 " + (event.getTotalApplicantLimit() - totalApplicantCount) + " 명만 가능합니다.");
		}

		Schedule schedule = Schedule.builder()
			.eventId(event.getId())
			.applicantLimit(request.getApplicantLimit())
			.applicantCount(0)
			.date(request.getDate())
			.build();

		scheduleRepository.save(schedule);
	}

	@Transactional
	public void changeSchedule(User user, ScheduleDto.Request request, int eventId, int scheduleId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new ValidationException("찾을수 없는 이벤트입니다."));

		Schedule currentSchedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ValidationException("찾을 수 없는 스케줄입니다."));
		if (!isEventContainsSchedule(user, event, currentSchedule)) {
			throw new ValidationException("유저 권한이 없거나, 이벤트에 속한 스케줄이 아닙니다.");
		}

		List<Schedule> scheduleList = scheduleRepository.findByEventId(eventId);
		int totalApplicantCount = scheduleList.stream().mapToInt(Schedule::getApplicantLimit).sum() - currentSchedule.getApplicantLimit();

		if (event.getTotalApplicantLimit() < totalApplicantCount + request.getApplicantLimit()) {
			throw new ValidationException(
				"최대 참여 가능 수를 넘을 수 없습니다. 현재 " + (event.getTotalApplicantLimit() - totalApplicantCount) + " 명만 가능합니다.");
		}

		if(request.getApplicantLimit() < currentSchedule.getApplicantCount()) {
			throw new ValidationException("현재 참여 신청한 수 보다 많아야 합니다.");
		}

		currentSchedule.setApplicantLimit(request.getApplicantLimit());
		currentSchedule.setDate(request.getDate());

		scheduleRepository.save(currentSchedule);
	}
	
	@Transactional
	public void removeSchedule(User user, int eventId, int scheduleId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new ValidationException("찾을수 없는 이벤트입니다."));

		Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ValidationException("찾을 수 없는 스케줄입니다."));
		if (!isEventContainsSchedule(user, event, schedule)) {
			throw new ValidationException("유저 권한이 없거나, 이벤트에 속한 스케줄이 아닙니다.");
		}
		
		scheduleRepository.deleteById(scheduleId);
	}

	public boolean isEventContainsSchedule(User user, Event event, Schedule schedule) {
		if (event.getUserId() == user.getId() && schedule.getEventId() == event.getId()) {
			return true;
		}
		return false;
	}
}
