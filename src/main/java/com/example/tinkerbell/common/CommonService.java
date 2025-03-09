package com.example.tinkerbell.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonService {
	@Value("${open-api.holiday-key}")
	private String openApiKey;
	private final ObjectMapper objectMapper;

	public List<OpenApiHolidayResponse> getHolidays(HolidayQuery holidayQuery) {
		String result = getRawResponseFromOpenApi(holidayQuery);

		try {
			JsonNode root = objectMapper.readTree(result);
			JsonNode holidayInfo = root.get("response").get("body").get("items").get("item");
			List<OpenApiHolidayResponse> holidayList = new ArrayList<>();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

			// 공휴일이 없을 경우
			if (Objects.isNull(holidayInfo)) {
				return holidayList;
			}

			// 공휴일이 하루일 경우
			if (!holidayInfo.isArray()) {
				OpenApiHolidayResponse holiday = new OpenApiHolidayResponse();
				holiday.setDateName(holidayInfo.get("dateName").asText());
				holiday.setDate(LocalDate.parse(holidayInfo.get("locdate").asText(), formatter));
				holidayList.add(holiday);
				return holidayList;
			}

			// 공후일이 여러날이라 array 인 경우
			ArrayNode holidayInfoArray = (ArrayNode)holidayInfo;
			holidayInfoArray.forEach(jsonNode -> {
				OpenApiHolidayResponse holiday = new OpenApiHolidayResponse();
				holiday.setDateName(jsonNode.get("dateName").asText());
				holiday.setDate(LocalDate.parse(jsonNode.get("locdate").asText(), formatter));
				holidayList.add(holiday);
			});
			return holidayList;
		} catch (Exception e) {
			log.error("[공휴일 API] JSON 파싱 실패: ", e);
			throw new RuntimeException("공휴일 API 불러오기에 실패했습니다.");
		}
	}

	public String getRawResponseFromOpenApi(HolidayQuery holidayQuery) {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
			.build();

		return webClient.get()
			.uri(uriBuilder -> uriBuilder.queryParam("solYear", holidayQuery.getYear())
				.queryParam("solMonth", holidayQuery.getMonth())
				// 중복 인코딩 문제 해결 https://stackoverflow.com/questions/64805657/how-to-encode-properly-the-plus-sign-when-making-a-request-with-webflux-webc
				.queryParam("ServiceKey", "{openApiKey}")
				.queryParam("numOfRows", 20)
				.queryParam("_type", "json")
				.build(openApiKey))
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}
}
