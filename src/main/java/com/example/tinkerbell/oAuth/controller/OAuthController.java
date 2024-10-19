package com.example.tinkerbell.oAuth.controller;

import org.springframework.http.ResponseCookie;
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
		TokenDto tokenDto = oAuthService.getAuthToken(code);

		String accessTokenCookie = ResponseCookie.from("accessToken", tokenDto.getAccessToken())
			.domain(DOMAIN)
			.path("/")
			.build()
			.toString();

		String refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
			.domain(DOMAIN)
			.path("/")
			.build()
			.toString();

		response.addHeader("set-Cookie", accessTokenCookie);
		response.addHeader("set-Cookie", refreshTokenCookie);

		response.sendRedirect(DOMAIN);
	}
}
