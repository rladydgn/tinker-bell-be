package com.example.tinkerbell.common;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OpenApiHolidayResponse {
	private LocalDate date;
	private String dateName;
}
