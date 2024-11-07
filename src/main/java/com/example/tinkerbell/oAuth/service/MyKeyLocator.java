package com.example.tinkerbell.oAuth.service;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.example.tinkerbell.oAuth.dto.ApplePublicKeyResponseDto;

import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;

public class MyKeyLocator extends LocatorAdapter<Key> {
	private List<ApplePublicKeyResponseDto> publicKeyList;

	public MyKeyLocator(List<ApplePublicKeyResponseDto> publicKeyList) {
		this.publicKeyList = publicKeyList;
	}

	@Override
	protected Key locate(ProtectedHeader header) {
		String keyId = header.getKeyId();
		Optional<ApplePublicKeyResponseDto> optionalPublicKey = publicKeyList.stream().filter(applePublicKey ->
			applePublicKey.getKid().equals(header.getKeyId())
		).findFirst();

		if (optionalPublicKey.isEmpty()) {
			throw new RuntimeException(
				"일치하는 public key 가 없습니다. " + header.getKeyId() + ", " + publicKeyList.toString());
		}

		ApplePublicKeyResponseDto publicKey = optionalPublicKey.get();

		BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(publicKey.getN()));
		BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(publicKey.getE()));

		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			KeySpec keySpec = new RSAPrivateKeySpec(n, e);

			return keyFactory.generatePublic(keySpec);
		} catch (Exception error) {
			throw new RuntimeException("[애플 로그인] public key 추출 실패", error);
		}

	}
}
