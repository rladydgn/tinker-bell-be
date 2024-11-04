package com.example.tinkerbell.oAuth.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.tinkerbell.oAuth.dto.AppleTokenResponseDto;
import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppleOAuthService {
	private static final long THIRTY_DAYS_MS = 30L * 24L * 60L * 60L * 1000L;
	private final OAuthService oAuthService;
	private final UserRepository userRepository;
	private final ResourceLoader resourceLoader;
	@Value("${oauth.apple.client-id}")
	private String clientId;
	@Value("${oauth.apple.team-id}")
	private String teamId;
	@Value("${oauth.apple.secret}")
	private String appleSecret;
	@Value("${jwt.secret}")
	private String secret;

	public AppleTokenResponseDto getAppleToken(String code) {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://appleid.apple.com/auth/token")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
			.build();

		try {
			return webClient.post()
				.uri(uriBuilder -> uriBuilder.path("/oauth/token")
					.queryParam("grant_type", "authorization_code")
					.queryParam("client_id", clientId)
					.queryParam("client_secret", makeClientSecretToken())
					.queryParam("code", code)
					.build())
				.retrieve()
				.bodyToMono(AppleTokenResponseDto.class)
				.block();
		} catch (WebClientResponseException e) {
			log.error("[애플 로그인 실패]: " + e.getResponseBodyAsString(), e);
			throw e;
		}
	}

	public TokenDto getAuthToken(String code, String domain) {
		AppleTokenResponseDto appleTokenDto = getAppleToken(code);
		User user = getUser(appleTokenDto);

		if (userRepository.findByEmailAndProvider(user.getEmail(), user.getProvider()).isEmpty()) {
			userRepository.save(user);
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

	public User getUser(AppleTokenResponseDto appleTokenResponseDto) {
		Claims claims = Jwts.parser()
			.verifyWith(this.getSecret())
			.build()
			.parseSignedClaims(appleTokenResponseDto.getIdToken())
			.getPayload();

		log.info("[애플 로그인] 유저정보: " + claims.toString());
		return User.builder()
			.email(claims.get("email").toString())
			.provider("apple")
			.build();
	}

	public String makeClientSecretToken() {
		String token = Jwts.builder()
			.subject(clientId) // sub
			.issuer(teamId) // iss
			.issuedAt(new Date()) // iat
			.expiration(new Date(System.currentTimeMillis() + THIRTY_DAYS_MS)) // exp
			.audience() // aud
			.add("https://appleid.apple.com")
			.and()
			.signWith(this.getSecret(), SignatureAlgorithm.ES256)
			.header()
			.keyId(appleSecret)
			.and()
			.compact();
		log.info("[애플 로그인] 로그인 요청 인증 토큰: " + token);
		return token;
	}

	private SecretKey getSecret() {
		try {
			ClassPathResource resource = new ClassPathResource("ticketbell.p8");
			InputStream inputStream = resource.getInputStream();
			// 가져온 파일 내용을 base64로 디코드
			byte[] bytes = inputStream.readAllBytes();
			// byte[] bytes = Decoders.BASE64.decode(inputStream.readAllBytes().toString());
			return Keys.hmacShaKeyFor(bytes);
		} catch (IOException e) {
			log.info("[애플 로그인]: pk 파일 로딩 실패", e);
			throw new RuntimeException("애플 로그인 실패");
		}
	}
}
