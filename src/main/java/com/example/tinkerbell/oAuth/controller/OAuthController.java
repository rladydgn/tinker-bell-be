package com.example.tinkerbell.oAuth.controller;

import com.example.tinkerbell.oAuth.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/redirect")
    public String redirect(@RequestParam("code") String code) throws Exception {
        this.oAuthService.getAuthToken(code);
        return code;
    }

    @GetMapping("/token")
    public String getToken() {
        return "hello";
    }
}
