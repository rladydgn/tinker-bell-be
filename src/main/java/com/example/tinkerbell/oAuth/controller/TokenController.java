package com.example.tinkerbell.oAuth.controller;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.dto.AccessTokenDto;
import com.example.tinkerbell.oAuth.dto.RefreshTokenDto;
import com.example.tinkerbell.oAuth.entity.User;
import com.example.tinkerbell.oAuth.service.OAuthService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
	private final OAuthService oAuthService;

	@Operation(summary = "토큰 검증", description = "토큰 검증")
	@GetMapping("/verify")
	public ResponseEntity<Void> verifyToken(@Login User user) {
		if (Objects.isNull(user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else {
			return ResponseEntity.ok().build();
		}
	}

	@Operation(summary = "accessToken 재발급")
	@PostMapping("/renew")
	public ResponseEntity<AccessTokenDto> renewAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
		return ResponseEntity.ok().body(oAuthService.renewAccessToken(refreshTokenDto));
	}
}
