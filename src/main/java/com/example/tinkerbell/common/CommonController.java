package com.example.tinkerbell.common;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class CommonController {
	private final CommonService commonService;

	@Operation(summary = "공휴일 조회 api", description = "연, 월을 파라미터로 입력하면 응답으로 공휴일인 날을 반환한다. 월은 10 미만일 경우 반드시 앞에 0을 붙여야 한다. ex. year: 2025, month: 01")
	@GetMapping("holidays")
	public ResponseEntity<List<OpenApiHolidayResponse>> getHolidays(HolidayQuery holidayQuery) {
		return ResponseEntity.ok(commonService.getHolidays(holidayQuery));
	}
}
