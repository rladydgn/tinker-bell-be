package com.example.tinkerbell.event.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final ScheduleRepository scheduleRepository;

	@Transactional
	public void saveFirstEventAndSchedules(EventDto.InitRequest eventDto, User user) {
		int scheduleApplicantLimit = eventDto.getScheduleDtoList().stream().map(
				ScheduleDto.Request::getApplicantLimit)
			.reduce(0, Integer::sum);
		if (eventDto.getTotalApplicantLimit() < scheduleApplicantLimit) {
			throw new ValidationException("스케줄 합 인원수("
				+ scheduleApplicantLimit
				+ ")는 행사 총 인원수("
				+ eventDto.getTotalApplicantLimit()
				+ ") 를 넘을 수 없습니다.");
		}

		Event event = Event.builder()
			.title(eventDto.getTitle())
			.totalApplicantLimit(eventDto.getTotalApplicantLimit())
			.userId(user.getId())
			.build();
		this.eventRepository.save(event);

		log.info("이벤트 첫 저장: " + event);

		List<Schedule> scheduleList = new ArrayList<>();
		eventDto.getScheduleDtoList().forEach(scheduleDto -> {
			Schedule schedule = Schedule.builder()
				.eventId(event.getId())
				.applicantLimit(scheduleDto.getApplicantLimit())
				.applicantCount(0)
				.date(scheduleDto.getDate())
				.build();
			scheduleList.add(schedule);
		});
		this.scheduleRepository.saveAll(scheduleList);

		log.info("이벤트 스케줄 첫 저장: " + scheduleList);
	}

	@Transactional(readOnly = true)
	public List<EventDto.Response> getEvents(User user) {
		// FIXME: 쿼리 조인이 발생하지 않고 있음
		List<Event> eventList = eventRepository.findAllByUserIdOrderByCreatedAt(user.getId());
		List<EventDto.Response> eventDtoResponse = new ArrayList<>();
		eventList.forEach(event -> {
			if (event.getScheduleList().size() == 0) {
				throw new ValidationException("스케줄이 존재하지 않습니다.");
			}

			List<ScheduleDto.Response> scheduleDtoList = event.getScheduleList().stream().map(ScheduleDto::toResponse).toList();
			Pair<LocalDateTime, LocalDateTime> startDateAndEndDatePair = getStartDateAndEndDate(event.getScheduleList());

			EventDto.Response response = EventDto.toResponse(event, startDateAndEndDatePair.getLeft(), startDateAndEndDatePair.getRight(), scheduleDtoList);

			eventDtoResponse.add(response);
		});

		return eventDtoResponse;
	}

	@Transactional(readOnly = true)
	public EventDto.Response getEvent(int id, User user) {
		Event event = eventRepository.findOneByIdAndUserId(id, user.getId()).orElseThrow(() -> new RuntimeException("찾을수 없는 이벤트 입니다."));
		List<ScheduleDto.Response> scheduleDtoList = event.getScheduleList().stream().map(ScheduleDto::toResponse).toList();
		Pair<LocalDateTime, LocalDateTime> startDateAndEndDatePair = getStartDateAndEndDate(event.getScheduleList());
		return EventDto.toResponse(event, startDateAndEndDatePair.getLeft(), startDateAndEndDatePair.getRight(), scheduleDtoList);
	}

	private Pair<LocalDateTime, LocalDateTime> getStartDateAndEndDate(List<Schedule> scheduleList) {
		LocalDateTime startDate = scheduleList.getFirst().getDate();
		LocalDateTime endDate = scheduleList.getFirst().getDate();

		for (Schedule schedule : scheduleList) {
			if (schedule.getDate().isBefore(startDate)) {
				startDate = schedule.getDate();
			}
			if (schedule.getDate().isAfter(endDate)) {
				endDate = schedule.getDate();
			}
		}

		return Pair.of(startDate, endDate);
	}
}
