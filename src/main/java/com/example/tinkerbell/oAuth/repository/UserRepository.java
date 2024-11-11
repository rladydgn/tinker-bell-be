package com.example.tinkerbell.oAuth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tinkerbell.oAuth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmailAndProvider(String email, String provider);

	Optional<User> findByAuthIdAndProvider(String authId, String provider);
}
