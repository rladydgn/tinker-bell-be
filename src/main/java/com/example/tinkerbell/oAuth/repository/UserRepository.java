package com.example.tinkerbell.oAuth.repository;

import com.example.tinkerbell.oAuth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, String provider);
}
