package com.example.tinkerbell.oAuth.controller;

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

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
	private final OAuthService oAuthService;
	@Value("${fe.url}")
	private String feUrl;

	@GetMapping("/redirect")
	public void redirect(@RequestParam("code") String code,
		HttpServletRequest request, HttpServletResponse response) throws Exception {
		TokenDto tokenDto = oAuthService.getAuthToken(code);
		Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.getAccessToken());
		accessTokenCookie.setPath("/");
		response.addCookie(accessTokenCookie);

		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
		refreshTokenCookie.setPath("/");
		response.addCookie(refreshTokenCookie);

		String redirectUrl = request.getHeader("referer");
		if (StringUtils.isEmpty(redirectUrl)) {
			response.sendRedirect(feUrl);
		}
		response.sendRedirect(redirectUrl);
	}
}
