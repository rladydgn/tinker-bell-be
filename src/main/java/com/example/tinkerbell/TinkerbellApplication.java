package com.example.tinkerbell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TinkerbellApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinkerbellApplication.class, args);
	}

}
