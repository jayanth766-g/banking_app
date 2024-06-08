package com.demo.thebankingproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.thebankingproject.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
	Boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	Boolean existsByAccountNumber(String accountNumber);
	User findByAccountNumber(String accountNumber);
}
