package com.example.shopping.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping.domain.dto.UserDto;
import com.example.shopping.domain.entity.user.EmailVerification;
import com.example.shopping.domain.entity.user.User;
import com.example.shopping.domain.entity.user.UserAuth;
import com.example.shopping.domain.entity.user.UserProfile;
import com.example.shopping.domain.repository.EmailVerificationRepository;
import com.example.shopping.domain.repository.UserAuthRepository;
import com.example.shopping.domain.repository.UserProfileRepository;
import com.example.shopping.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserAuthRepository userAuthRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    // 비밀번호 변경 (로그인 상태)
    @Transactional
    public void changePassword(Long userId, UserDto.ChangePassword request) {
        UserAuth userAuth = userAuthRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 정보 없음"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), userAuth.getPasswordHash())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        userAuth.updatePassword(passwordEncoder.encode(request.getNewPassword())); // *Entity에 메서드 추가 필요
    }

    // 프로필 수정
    @Transactional
    public void updateProfile(Long userId, UserDto.UpdateProfile request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("프로필 없음"));
        
        // 이름 수정
        if (request.getName() != null) {
             profile.updateName(request.getName()); // *Entity 메서드 추가 필요
        }
        // 전화번호 등은 User 테이블에 있다면 User도 조회해서 수정해야 함
    }

    // 회원 탈퇴 (Soft Delete)
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        user.withdraw(); // status -> WITHDRAWN
    }

    // 비밀번호 찾기 1: 인증코드 발송
    @Transactional
    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email) // *Repository 메서드 추가 필요
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        String code = UUID.randomUUID().toString().substring(0, 6); // 6자리 랜덤 코드

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .verificationCode(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // 5분 유효
                .isVerified(false)
                .build();
        
        emailVerificationRepository.save(verification);
        mailService.sendEmail(email, "[쇼핑앱] 비밀번호 찾기 인증코드", "인증코드: " + code);
    }

    // 비밀번호 찾기 2: 코드 검증 및 비밀번호 초기화
    @Transactional
    public void resetPassword(UserDto.ResetPassword request) {
        // 1. 인증코드 검증
        EmailVerification ev = emailVerificationRepository.findByEmailAndVerificationCode(request.getEmail(), request.getVerificationCode())
                .orElseThrow(() -> new RuntimeException("잘못된 인증코드입니다."));

        if (ev.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증코드가 만료되었습니다.");
        }

        ev.verify(); // 사용 처리

        // 2. 비밀번호 변경
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserAuth userAuth = userAuthRepository.findById(user.getUserId()).orElseThrow();
        
        userAuth.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}