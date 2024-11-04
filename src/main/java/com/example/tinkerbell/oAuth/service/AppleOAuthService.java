package com.example.tinkerbell.oAuth.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
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
import io.jsonwebtoken.io.Decoders;
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

	@Value("${oauth.apple.private-key}")
	private String privateKey;

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
			.decryptWith(getPrivateKey())
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
			.header()
			.keyId(appleSecret)
			.and()
			.signWith(getPrivateKey(), Jwts.SIG.ES256)
			.compact();
		log.info("[애플 로그인] 로그인 요청 인증 토큰: " + token);
		return token;
	}

	private PrivateKey getPrivateKey() {
		try {
			// Resource resource = resourceLoader.getResource("ticketbell.txt");
			// InputStream inputStream = resource.getInputStream();
			// String privateKey = inputStream.readAllBytes().toString();
			//
			// log.info("pk s: " + privateKey);
			//
			// privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
			// 	.replace("-----END PRIVATE KEY-----", "")
			// 	.replaceAll("\\s", "");
			//
			// log.info("pk: " + privateKey);

			byte[] privateKeyBytes = Decoders.BASE64.decode(privateKey);

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			return keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			log.info("[애플 로그인]: PK 생성 실패", e);
			throw new RuntimeException("애플 로그인 실패");
		}
	}
}
