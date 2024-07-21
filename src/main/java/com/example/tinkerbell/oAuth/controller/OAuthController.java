package com.example.tinkerbell.oAuth.controller;

import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    @Autowired
    private OAuthService oAuthService;
    @Value("${fe.url}")
    private String feUrl;

    @GetMapping("/redirect")
    public void redirect(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        TokenDto tokenDto = this.oAuthService.getAuthToken(code);
        response.sendRedirect(feUrl + "?accessToken=" + tokenDto.getAccessToken()
                + "&refreshToken=" + tokenDto.getRefreshToken());
    }

    @GetMapping("/token")
    public String getToken() {
        return "hello";
    }
}
