package com.example.tinkerbell.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.tinkerbell.exception.dto.ErrorResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleException(Exception exception) {
		ErrorResponseDto errorResponseDto = new ErrorResponseDto();
		errorResponseDto.setCode(HttpStatus.BAD_REQUEST.value());
		errorResponseDto.setMessage(exception.getMessage());
		log.warn("error", exception);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
	}
}
