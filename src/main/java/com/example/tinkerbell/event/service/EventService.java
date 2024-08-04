package com.example.tinkerbell.event.service;

import com.example.tinkerbell.event.dto.EventDto;
import com.example.tinkerbell.event.entity.Event;
import com.example.tinkerbell.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    public void saveEvent(EventDto.Request eventDtoRequest) {
        Event event = Event.builder()
                .title(eventDtoRequest.getTitle())
                .totalPeopleNumber(eventDtoRequest.getTotalPeopleNumber())
                .build();
        this.eventRepository.save(event);
    }
}
