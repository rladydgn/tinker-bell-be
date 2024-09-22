package com.example.tinkerbell.exception.dto;

import lombok.Data;

@Data
public class ErrorResponseDto {
	private int code;
	private String message;
}
