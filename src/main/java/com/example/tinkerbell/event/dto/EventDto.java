package com.example.tinkerbell.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class EventDto {
    @Data
    public static class Request {
        @NotBlank
        private String title;
        private int totalPeopleNumber;
    }

    @Data
    public static class InitRequest {
        @NotBlank
        private String title;
        @Min(value = 1)
        @Max(value = 3000)
        private int totalPeopleNumber;
        private List<ScheduleDto.Request> scheduleDtoList;
    }

    @Data
    @Builder
    public static class Response {
        private int id;
        private String title;
        private int totalPeopleNumber;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<ScheduleDto.Response> scheduleDtoList;
    }
}