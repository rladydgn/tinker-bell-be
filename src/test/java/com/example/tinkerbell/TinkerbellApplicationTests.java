package com.example.tinkerbell;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.tinkerbell.oAuth.dto.ApplePublicKeyResponseDto;

@SpringBootTest
class TinkerbellApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test() {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://appleid.apple.com")
			.build();

		ApplePublicKeyResponseDto applePublicKeyList = webClient.get()
			.uri(uriBuilder -> uriBuilder.path("/auth/keys").build())
			.retrieve()
			.bodyToMono(ApplePublicKeyResponseDto.class).block();

		System.out.println(applePublicKeyList.toString());
	}

}
