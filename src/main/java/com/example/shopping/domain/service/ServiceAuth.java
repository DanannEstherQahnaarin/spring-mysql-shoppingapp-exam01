package com.example.shopping.domain.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.shopping.domain.dto.AuthDto;
import com.example.shopping.domain.entity.user.User;
import com.example.shopping.domain.entity.user.UserAuth;
import com.example.shopping.domain.entity.user.UserProfile;
import com.example.shopping.domain.enums.JoinType;
import com.example.shopping.domain.enums.UserStatus;
import com.example.shopping.domain.repository.UserAuthRepository;
import com.example.shopping.domain.repository.UserProfileRepository;
import com.example.shopping.domain.repository.UserRepository;
import com.example.shopping.global.security.JwtTokenProvider;

import jakarta.transaction.Transactional;

public class ServiceAuth {
    private UserRepository userRepository;
    private UserAuthRepository userAuthRepository;
    private UserProfileRepository userProfileRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    @Transactional
    public Long signUp(AuthDto.SignupRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {

        }

        if (userRepository.existsByEmail(request.getEmail())) {

        }

        User user = User.builder()
                .loginId(request.getLoginId())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .joinType(JoinType.LOCAL)
                .build();

        User saveUser = userRepository.save(user);

        UserProfile userProfile = UserProfile.builder()
                .user(saveUser)
                .name(request.getName())
                .build();

        userProfileRepository.save(userProfile);

        UserAuth userAuth = UserAuth.builder()
                .user(saveUser)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userAuthRepository.save(userAuth);

        return saveUser.getUserId();
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId()).orElseThrow();
        UserAuth uAuth = userAuthRepository.findById(user.getUserId()).orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), uAuth.getPasswordHash())) {

        }

        user.updateLastLogin();
        String token = tokenProvider.createToken(String.valueOf(user.getUserId()), "User_Role");
        return new AuthDto.TokenResponse(token);
    }
}
