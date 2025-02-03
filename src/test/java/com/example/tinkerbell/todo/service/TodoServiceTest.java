package com.example.tinkerbell.todo.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.tinkerbell.oAuth.entity.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoServiceTest {
	User user;

	@BeforeAll
	void init() {
		this.user = User.builder()
			.id(1)
			.nickname("test")
			.email("test@test.com")
			.provider("test")
			.authId("test")
			.build();

		System.out.println(this.user);
	}

	@Test
	void sampleTest() {

	}
}
