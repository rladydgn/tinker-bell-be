package com.example.tinkerbell.oAuth.controller;

import com.example.tinkerbell.oAuth.annotation.Login;
import com.example.tinkerbell.oAuth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(@Login User user) {
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
