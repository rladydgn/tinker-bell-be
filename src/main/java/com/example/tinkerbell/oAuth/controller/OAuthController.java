package com.example.tinkerbell.oAuth.controller;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
import lombok.extern.slf4j.Slf4j;

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

		Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.getAccessToken());
		accessTokenCookie.setPath("/");

		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
		refreshTokenCookie.setPath("/");

		// 쿠키 허용 도메인
		URI uri = new URI(redirectUrl);
		String domain = uri.getHost();
		if (domain.contains("www")) {
			domain = domain.replace("www", "");
			accessTokenCookie.setDomain(domain);
			refreshTokenCookie.setDomain(domain);
			log.info("check: " + domain);
		}

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		response.sendRedirect(redirectUrl);
	}
}
