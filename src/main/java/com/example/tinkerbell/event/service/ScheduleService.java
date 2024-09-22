package com.example.tinkerbell.event.service;

import com.example.tinkerbell.event.dto.ScheduleDto;
import com.example.tinkerbell.event.entity.Event;
import com.example.tinkerbell.event.entity.Schedule;
import com.example.tinkerbell.event.repository.EventRepository;
import com.example.tinkerbell.event.repository.ScheduleRepository;
import com.example.tinkerbell.oAuth.entity.User;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

		if (!isValid(user, event, schedule)) {
			throw new ValidationException("유저 권한이 없거나, 이벤트에 속한 스케줄이 아닙니다.");
		}

		return ScheduleDto.Response.builder()
			.id(schedule.getId())
			.applicantLimit(schedule.getApplicantLimit())
			.date(schedule.getDate())
			.build();
	}

	//    @Transactional
	//    public ScheduleDto.Response changeSchedule(User user, int eventId, int scheduleId, ScheduleDto.Request scheduleRequest) {
	//        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ValidationException("존재하지 않는 이벤트 입니다."));
	//        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ValidationException("찾을 수 없는 스케줄 입니다."));
	//
	//        if(!isValid(user, event, schedule)) {
	//            throw new ValidationException("유저 권한이 없거나, 이벤트에 속한 스케줄이 아닙니다.");
	//        }
	//    }

	public boolean isValid(User user, Event event, Schedule schedule) {
		if (event.getUserId() == user.getId() && schedule.getEventId() == event.getId()) {
			return true;
		}
		return false;
	}
}
