package com.example.tinkerbell.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

public class ScheduleDto {
    @Data
    @ToString
    public static class Request {
        @Min(value = 1)
        @Max(value = 3000)
        private int peopleNumber;
        private LocalDateTime date;
    }

    @Data
    @Builder
    public static class Response {
        private int id;
        private int peopleNumber;
        private LocalDateTime date;
    }
}



