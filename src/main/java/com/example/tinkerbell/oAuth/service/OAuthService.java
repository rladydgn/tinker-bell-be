package com.example.tinkerbell.oAuth.service;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.tinkerbell.oAuth.dto.KaKaoTokenResponseDto;
import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {
	//github.com/jwtk/jjwt (JWTs 문서)
	private static final long ThirtyDaysInMs = 30l * 24l * 60l * 60l * 1000l;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	@Value("${oauth.kakao.client-id}")
	private String clientId;
	@Value("${oauth.kakao.redirect-url}")
	private String redirectUrl;
	@Value("${jwt.secret}")
	private String secret;

	public KaKaoTokenResponseDto getOAuthToken(String code) {
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

	public User getUserInfo(KaKaoTokenResponseDto kaKaoTokenResponseDto) throws Exception {
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
			String nickname = root.path("kakao_account").path("profile").path("nickname").asText();
			String email = root.path("kakao_account").path("email").asText();
			return User.builder().nickname(nickname).email(email).provider("kakao").build();
		} catch (Exception e) {
			log.error("[카카오 로그인 유저 정보 가져오기 실패] " + e.getMessage());
			throw e;
		}
	}

	public TokenDto getAuthToken(String code) throws Exception {
		KaKaoTokenResponseDto kaKaoTokenResponseDto = getOAuthToken(code);
		User user = getUserInfo(kaKaoTokenResponseDto);
		if (userRepository.findByEmailAndProvider(user.getEmail(), "kakao").isEmpty()) {
			userRepository.save(user);
		}

		return makeToken(user);
	}

	public TokenDto makeToken(User user) {
		String accessToken = Jwts.builder()
			.subject(UUID.randomUUID().toString())
			.claim("email", user.getEmail())
			.claim("provider", user.getProvider())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ThirtyDaysInMs))
			.signWith(this.getSecret())
			.compact();

		String refreshToken = Jwts.builder()
			.subject(UUID.randomUUID().toString())
			.claim("email", user.getEmail())
			.claim("provider", user.getProvider())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ThirtyDaysInMs * 3))
			.signWith(this.getSecret())
			.compact();
		
		log.info("acc: " + accessToken);
		log.info("res: " + refreshToken);

		return TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
	}

	public boolean verifyToken(String token) {
		return Jwts
			.parser()
			.verifyWith(this.getSecret())
			.build()
			.parseSignedClaims(token)
			.getPayload().getExpiration().after(new Date());
	}

	public User getUserFromToken(String token) {
		Claims claims = Jwts
			.parser()
			.verifyWith(this.getSecret())
			.build()
			.parseSignedClaims(token)
			.getPayload();

		return this.userRepository.findByEmailAndProvider(claims.get("email").toString(),
				claims.get("provider").toString())
			.orElseThrow(() -> new ValidationException("유저를 찾을 수 없습니다."));
	}

	public String getToken(String token) {
		if (Objects.isNull(token)) {
			throw new ValidationException("토큰 인증 실패: 토큰값이 존재하지 않음");
		}

		String[] str = token.split(" ");
		if (str.length != 2 || !str[0].equals("Bearer")) {
			throw new ValidationException("지원하지 않는 토큰 타입입니다.");
		}
		return str[1];
	}

	private SecretKey getSecret() {
		byte[] bytes = Decoders.BASE64.decode(this.secret);
		return Keys.hmacShaKeyFor(bytes);
	}
}
