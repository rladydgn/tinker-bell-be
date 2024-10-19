package com.example.tinkerbell.oAuth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.service.OAuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
	private final OAuthService oAuthService;
	private final String DOMAIN = "ticketbell.store";

	@Operation(summary = "oauth 로그인 리다이렉트", description = "oauth 로그인 리다이렉트(kakao)")
	@GetMapping("/redirect")
	public void redirect(@RequestParam("code") String code,
		HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 쿠키 허용 도메인
		TokenDto tokenDto = oAuthService.getAuthToken(code, DOMAIN);

		response.addHeader("set-Cookie", tokenDto.getAccessToken());
		response.addHeader("set-Cookie", tokenDto.getRefreshToken());

		response.sendRedirect(DOMAIN);
	}
}
