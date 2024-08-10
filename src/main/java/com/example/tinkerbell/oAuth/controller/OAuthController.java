package com.example.tinkerbell.oAuth.controller;

import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.dto.TokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;
    @Value("${fe.url}")
    private String feUrl;

    @GetMapping("/redirect")
    public void redirect(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        TokenDto tokenDto = oAuthService.getAuthToken(code);
        response.sendRedirect(feUrl + "?accessToken=" + tokenDto.getAccessToken()
                + "&refreshToken=" + tokenDto.getRefreshToken());
    }

    @GetMapping("/token")
    public String getToken() {
        return "hello";
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(@Login User user) {
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
