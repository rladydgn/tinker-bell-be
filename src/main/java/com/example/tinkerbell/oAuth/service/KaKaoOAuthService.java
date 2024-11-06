package com.example.tinkerbell.oAuth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.tinkerbell.oAuth.dto.KaKaoTokenResponseDto;
import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KaKaoOAuthService {
	//github.com/jwtk/jjwt (JWTs 문서)
	private final ObjectMapper objectMapper;
	private final OAuthService oAuthService;
	private final UserRepository userRepository;
	@Value("${oauth.kakao.client-id}")
	private String clientId;
	@Value("${oauth.kakao.redirect-url}")
	private String redirectUrl;
	@Value("${jwt.secret}")
	private String secret;

	public KaKaoTokenResponseDto getKaKaoToken(String code) {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://kauth.kakao.com")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
			.build();

		try {
			return webClient.post()
				.uri(uriBuilder -> uriBuilder.path("/oauth/token")
					.queryParam("grant_type", "authorization_code")
					.queryParam("client_id", clientId)
					.queryParam("redirect_uri", redirectUrl)
					.queryParam("code", code)
					.build())
				.retrieve()
				.bodyToMono(KaKaoTokenResponseDto.class)
				.block();
		} catch (WebClientResponseException e) {
			log.error("[카카오 로그인 토큰 발급 실패] " + e.getMessage());
			throw e;
		}
	}

	public User getUser(KaKaoTokenResponseDto kaKaoTokenResponseDto) throws Exception {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://kapi.kakao.com")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kaKaoTokenResponseDto.getAccessToken())
			.build();

		try {
			String res = webClient.post()
				.uri(uriBuilder -> uriBuilder.path("/v2/user/me").build())
				.retrieve()
				.bodyToMono(String.class)
				.block();

			// kakao 에서 받은 유저 정보 파싱
			JsonNode root = objectMapper.readTree(res);
			log.info("[카카오 유저 정보]: " + root.toString());
			String id = root.path("id").asText();
			String nickname = root.path("kakao_account").path("profile").path("nickname").asText();
			String email = root.path("kakao_account").path("email").asText();
			return User.builder().nickname(nickname).email(email).provider("kakao").build();
		} catch (Exception e) {
			log.error("[카카오 로그인 유저 정보 가져오기 실패] " + e.getMessage());
			throw e;
		}
	}

	public TokenDto getAuthToken(String code, String domain) throws Exception {
		KaKaoTokenResponseDto kaKaoTokenResponseDto = getKaKaoToken(code);
		User user = getUser(kaKaoTokenResponseDto);

		Optional<User> savedUser = userRepository.findByEmailAndProvider(user.getEmail(), "kakao");
		if (savedUser.isEmpty()) {
			userRepository.save(user);
		} else {
			// auth_id 저장용 로직 추후 삭제
			savedUser.get().setAuthId(user.getAuthId());
			userRepository.save(savedUser.get());
		}

		TokenDto tokenDto = oAuthService.makeToken(user);

		tokenDto.setAccessToken(ResponseCookie.from("accessToken", tokenDto.getAccessToken())
			.domain(domain)
			.path("/")
			.build()
			.toString());

		tokenDto.setRefreshToken(ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
			.domain(domain)
			.path("/")
			.build()
			.toString());

		return tokenDto;
	}
}
