package com.example.tinkerbell.oAuth.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.service.OAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
	private final OAuthService oAuthService;
	@Value("${fe.url}")
	private String feUrl;

	@GetMapping("/redirect")
	public void redirect(@RequestParam("code") String code,
		HttpServletRequest request, HttpServletResponse response) throws Exception {
		TokenDto tokenDto = oAuthService.getAuthToken(code);

		// 리다이렉트 URL
		String redirectUrl = request.getHeader("referer");
		if (StringUtils.isEmpty(redirectUrl)) {
			redirectUrl = feUrl;
		}

		// 쿠키 허용 도메인
		URI uri = new URI(redirectUrl);
		String domain = uri.getHost();
		if(domain.contains("www")) {
			domain.replace("www", "");
			log.info("check: " + domain);
		}

		String accessTokenCookie = ResponseCookie.from("accessToken", tokenDto.getAccessToken())
				.domain(domain)
				.path("/")
				.sameSite("lax")
				.build()
				.toString();

		String refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
				.domain(domain)
				.path("/")
				.sameSite("lax")
				.build()
				.toString();

		response.addHeader("set-Cookie", accessTokenCookie);
		response.addHeader("set-Cookie", refreshTokenCookie);

		response.sendRedirect(redirectUrl);
	}
}
