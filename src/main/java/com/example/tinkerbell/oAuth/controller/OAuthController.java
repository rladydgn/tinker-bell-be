package com.example.tinkerbell.oAuth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.service.AppleOAuthService;
import com.example.tinkerbell.oAuth.service.KaKaoOAuthService;
import com.example.tinkerbell.oAuth.service.OAuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
	private final OAuthService oAuthService;
	private final KaKaoOAuthService kaKaoOAuthService;
	private final AppleOAuthService appleOAuthService;
	@Value("${fe.url}")
	private String feUrl;

	@Operation(summary = "oauth 카카오 로그인 리다이렉트")
	@GetMapping("/redirect")
	public void redirect(@RequestParam("code") String code, HttpServletResponse response) throws Exception {

		// 쿠키 허용 도메인
		String domain = oAuthService.getDomain(feUrl);
		TokenDto tokenDto = kaKaoOAuthService.getAuthToken(code, domain);

		response.addHeader("set-Cookie", tokenDto.getAccessToken());
		response.addHeader("set-Cookie", tokenDto.getRefreshToken());

		response.sendRedirect(feUrl);
	}

	@Operation(summary = "oauth 애플 로그인 리다이렉트")
	@GetMapping("/redirect/apple")
	public void appleRedirect(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
		String domain = oAuthService.getDomain(feUrl);
		TokenDto tokenDto = appleOAuthService.getAuthToken(code, domain);

		response.addHeader("set-Cookie", tokenDto.getAccessToken());
		response.addHeader("set-Cookie", tokenDto.getRefreshToken());

		response.sendRedirect(feUrl);
	}
}
