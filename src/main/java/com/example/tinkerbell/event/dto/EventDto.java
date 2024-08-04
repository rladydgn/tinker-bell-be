package com.example.tinkerbell.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class EventDto {
    @Getter
    @Setter
    public static class Request {
        @NotBlank
        private String title;
        private int totalPeopleNumber;
    }
}
