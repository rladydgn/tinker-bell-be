package com.example.tinkerbell.event.service;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.dto.ScheduleDto;
import com.example.tinkerbell.event.entity.Event;
import com.example.tinkerbell.event.entity.Schedule;
import com.example.tinkerbell.event.repository.EventRepository;
import com.example.tinkerbell.event.repository.ScheduleRepository;
import com.example.tinkerbell.oAuth.entity.User;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final ScheduleRepository scheduleRepository;

	@Transactional
	public void saveFirstEventAndSchedules(EventDto.InitRequest eventDto, User user) {
		int schedulePeopleNumber = eventDto.getScheduleDtoList().stream().map(
				ScheduleDto.Request::getPeopleNumber)
			.reduce(0, Integer::sum);
		if (eventDto.getTotalPeopleNumber() < schedulePeopleNumber) {
			throw new ValidationException("스케줄 합 인원수("
				+ schedulePeopleNumber
				+ ")는 행사 총 인원수("
				+ eventDto.getTotalPeopleNumber()
				+ ") 를 넘을 수 없습니다.");
		}

		Event event = Event.builder()
			.title(eventDto.getTitle())
			.totalPeopleNumber(eventDto.getTotalPeopleNumber())
			.userId(user.getId())
			.build();
		this.eventRepository.save(event);

		log.info("이벤트 첫 저장: " + event);

		List<Schedule> scheduleList = new ArrayList<>();
		eventDto.getScheduleDtoList().forEach(scheduleDto -> {
			Schedule schedule = Schedule.builder()
				.eventId(event.getId())
				.peopleNumber(scheduleDto.getPeopleNumber())
				.date(scheduleDto.getDate())
				.build();
			scheduleList.add(schedule);
		});
		this.scheduleRepository.saveAll(scheduleList);

		log.info("이벤트 스케줄 첫 저장: " + scheduleList);
	}

	@Transactional(readOnly = true)
	public List<EventDto.Response> getEvents(User user) {
		// TODO: 쿼리 조인이 발생하지 않고 있음
		List<Event> eventList = eventRepository.findAllByUserIdOrderByCreatedAt(user.getId());
		List<EventDto.Response> eventDtoResponse = new ArrayList<>();
		eventList.forEach(event -> {
			if (event.getScheduleList().size() == 0) {
				throw new ValidationException("스케줄이 존재하지 않습니다.");
			}

			LocalDateTime startDate = event.getScheduleList().getFirst().getDate();
			LocalDateTime endDate = event.getScheduleList().getFirst().getDate();
			List<ScheduleDto.Response> scheduleDtoList = new ArrayList<>();

			for (Schedule schedule : event.getScheduleList()) {
				if (schedule.getDate().isBefore(startDate)) {
					startDate = schedule.getDate();
				}
				if (schedule.getDate().isAfter(endDate)) {
					endDate = schedule.getDate();
				}
				ScheduleDto.Response response = ScheduleDto.Response.builder()
					.id(schedule.getId())
					.peopleNumber(schedule.getPeopleNumber())
					.date(schedule.getDate())
					.build();
				scheduleDtoList.add(response);
			}

			EventDto.Response response = EventDto.Response.builder()
				.id(event.getId())
				.title(event.getTitle())
				.startDate(startDate)
				.endDate(endDate)
				.scheduleDtoList(scheduleDtoList)
				.build();

			eventDtoResponse.add(response);
		});

		return eventDtoResponse;
	}
}
