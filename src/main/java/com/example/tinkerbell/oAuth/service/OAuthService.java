package com.example.tinkerbell.oAuth.service;

import com.example.tinkerbell.oAuth.dto.KaKaoTokenResponseDto;
import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class OAuthService {
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.redirect-url}")
    private String redirectUrl;
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    private static final int EXPIRE_TIME = 24 * 60 * 1000;

    public KaKaoTokenResponseDto getOAuthToken(String code) {
        WebClient webClient = WebClient.builder().baseUrl("https://kauth.kakao.com").defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8").build();

        try {
            return webClient.post().uri(uriBuilder -> uriBuilder.path("/oauth/token").queryParam("grant_type", "authorization_code").queryParam("client_id", clientId).queryParam("redirect_uri", redirectUrl).queryParam("code", code).build()).retrieve().bodyToMono(KaKaoTokenResponseDto.class).block();
        } catch (WebClientResponseException e) {
            log.error("[카카오 로그인 토큰 발급 실패] " + e.getMessage());
            throw e;
        }
    }

    public User getUserInfo(KaKaoTokenResponseDto kaKaoTokenResponseDto) throws Exception {
        WebClient webClient = WebClient.builder().baseUrl("https://kapi.kakao.com").defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8").defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kaKaoTokenResponseDto.getAccessToken()).build();

        try {
            String res = webClient.post().uri(uriBuilder -> uriBuilder.path("/v2/user/me").build()).retrieve().bodyToMono(String.class).block();

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
                // 1일
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(this.getSecret())
                .compact();

        String refreshToken = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("email", user.getEmail())
                .claim("provider", user.getProvider())
                .issuedAt(new Date())
                // 1주일
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME * 7))
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
}
