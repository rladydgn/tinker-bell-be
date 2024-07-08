package com.example.tinkerbell;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("{hello}")
    private String he;

    @GetMapping
    public String hello() {
        System.out.println(he);
        return "hello";
    }
}
