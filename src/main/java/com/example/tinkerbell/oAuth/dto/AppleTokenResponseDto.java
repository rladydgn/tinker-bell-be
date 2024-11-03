package com.example.tinkerbell.oAuth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AppleTokenResponseDto {
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("expires_in")
	private String expiresIn;
	@JsonProperty("refresh_token")
	private String refreshToken;
	@JsonProperty("id_token")
	private String idToken;
}
