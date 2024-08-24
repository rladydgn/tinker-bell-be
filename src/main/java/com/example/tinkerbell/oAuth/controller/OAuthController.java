package com.example.tinkerbell.oAuth.controller;

import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.service.OAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;
    @Value("${fe.url}")
    private String feUrl;

    @GetMapping("/redirect")
    public void redirect(@RequestParam("code") String code, @RequestParam("redUrl") String redUrl, HttpServletResponse response) throws Exception {
        TokenDto tokenDto = oAuthService.getAuthToken(code);
        response.addCookie(new Cookie("accessToken", tokenDto.getAccessToken()));
        response.addCookie(new Cookie("refreshToken", tokenDto.getRefreshToken()));
        response.sendRedirect(feUrl);
    }
}
