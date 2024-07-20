package com.example.tinkerbell.oAuth.service;

import com.example.tinkerbell.oAuth.dto.KaKaoTokenResponseDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class OAuthService {
    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-url}")
    private String redirectUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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

    public void getAuthToken(String code) throws Exception {
        KaKaoTokenResponseDto kaKaoTokenResponseDto = getOAuthToken(code);
        User user = getUserInfo(kaKaoTokenResponseDto);
        if (userRepository.findByEmailAndProvider(user.getEmail(), "kakao").isEmpty()) {
            userRepository.save(user);
        }
    }
}
