package com.example.tinkerbell.oAuth.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.tinkerbell.oAuth.dto.AccessTokenDto;
import com.example.tinkerbell.oAuth.dto.RefreshTokenDto;
import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.repository.UserRepository;

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
	private static final long THIRTY_DAYS_MS = 30L * 24L * 60L * 60L * 1000L;
	@Value("${jwt.secret}")
	private String secret;
	private final UserRepository userRepository;

	public TokenDto makeToken(User user) {
		String accessToken = Jwts.builder()
			.subject(user.getAuthId())
			.claim("id", user.getAuthId())
			.claim("email", user.getEmail())
			.claim("provider", user.getProvider())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + THIRTY_DAYS_MS))
			.signWith(this.getSecret())
			.compact();

		String refreshToken = Jwts.builder()
			.subject(user.getAuthId())
			.claim("id", user.getAuthId())
			.claim("email", user.getEmail())
			.claim("provider", user.getProvider())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + THIRTY_DAYS_MS * 3))
			.signWith(this.getSecret())
			.compact();

		log.info("acc: " + accessToken);
		log.info("res: " + refreshToken);

		return TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
	}

	private SecretKey getSecret() {
		byte[] bytes = Decoders.BASE64.decode(this.secret);
		return Keys.hmacShaKeyFor(bytes);
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

		return this.userRepository.findByAuthIdAndProvider(claims.getSubject(),
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

	public String getDomain(String url) throws URISyntaxException {
		URI uri = new URI(url);
		return uri.getHost().replace("www", "");
	}

	public AccessTokenDto renewAccessToken(RefreshTokenDto refreshTokenDto) {
		if (verifyToken(refreshTokenDto.getRefreshToken())) {
			User user = getUserFromToken(refreshTokenDto.getRefreshToken());
			TokenDto tokenDto = makeToken(user);
			AccessTokenDto accessTokenDto = new AccessTokenDto();
			accessTokenDto.setAccessToken(tokenDto.getAccessToken());
			return accessTokenDto;
		}
		throw new RuntimeException("유효하지 않은 refresh token 입니다. :" + refreshTokenDto);
	}
}
