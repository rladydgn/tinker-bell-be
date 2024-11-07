package com.example.tinkerbell.oAuth.dto;

import lombok.Data;

@Data
public class ApplePublicKeyDto {
	private String alg;
	private String e;
	private String kid;
	private String kty;
	private String n;
	private String use;
}
