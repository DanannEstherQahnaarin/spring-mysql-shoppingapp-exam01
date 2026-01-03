package com.example.shopping.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopping.domain.entity.user.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String code);
    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email); // 가장 최근 인증 요청
}