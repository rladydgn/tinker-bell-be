package com.example.tinkerbell.event.service;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.dto.ScheduleDto;
import com.example.tinkerbell.event.entity.Event;
import com.example.tinkerbell.event.entity.Schedule;
import com.example.tinkerbell.event.repository.EventRepository;
import com.example.tinkerbell.event.repository.ScheduleRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void saveFirstEventAndSchedules(EventDto.InitRequest eventDto) {
        int schedulePeopleNumber = eventDto.getScheduleDtoList().stream().map(
                        ScheduleDto.InitRequest::getPeopleNumber)
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
}
