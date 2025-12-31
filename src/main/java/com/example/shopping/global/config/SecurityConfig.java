package com.example.shopping.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API)
                .csrf(AbstractHttpConfigurer::disable)
                // Form 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 관리: Stateless (JWT 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인은 누구나 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
