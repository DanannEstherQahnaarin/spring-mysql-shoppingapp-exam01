package com.example.shopping.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.domain.dto.AuthDto;
import com.example.shopping.domain.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 인증 컨트롤러
 * 
 * <p>
 * 사용자 인증 관련 REST API 엔드포인트를 제공하는 컨트롤러입니다.
 * 회원가입, 로그인 등의 기능을 처리합니다.
 * 
 * <p>
 * API 경로:
 * <ul>
 * <li>기본 경로: /api/auth</li>
 * </ul>
 * 
 * <p>
 * 제공 엔드포인트:
 * <ul>
 * <li>POST /api/auth/signup: 회원가입</li>
 * <li>POST /api/auth/login: 로그인 (JWT 토큰 발급)</li>
 * </ul>
 * 
 * <p>
 * 데이터 검증:
 * <ul>
 * <li>모든 요청 DTO는 @Valid 어노테이션으로 검증됩니다.</li>
 * <li>Jakarta Validation을 사용하여 필수 필드, 형식 등을 검증합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    /** 인증 서비스 */
    private final AuthService authService;

    /**
     * 회원가입 API
     * 
     * <p>
     * 새로운 사용자를 등록합니다.
     * 
     * <p>
     * 요청:
     * <ul>
     * <li>Content-Type: application/json</li>
     * <li>Body: AuthDto.SignupRequest (로그인 ID, 비밀번호, 이름, 이메일, 전화번호)</li>
     * </ul>
     * 
     * <p>
     * 응답:
     * <ul>
     * <li>Status: 200 OK</li>
     * <li>Body: 생성된 사용자 ID (Long)</li>
     * </ul>
     * 
     * <p>
     * 검증:
     * <ul>
     * <li>로그인 ID, 비밀번호, 이름은 필수 입력 항목입니다.</li>
     * <li>로그인 ID와 이메일은 중복될 수 없습니다.</li>
     * </ul>
     * 
     * @param request 회원가입 요청 DTO
     * @return 생성된 사용자 ID를 포함한 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody @Valid AuthDto.SignupRequest request) {
        Long id = authService.signUp(request);
        return ResponseEntity.ok(id);
    }

    /**
     * 로그인 API
     * 
     * <p>
     * 사용자 인증을 수행하고 JWT 토큰을 발급합니다.
     * 
     * <p>
     * 요청:
     * <ul>
     * <li>Content-Type: application/json</li>
     * <li>Body: AuthDto.LoginRequest (로그인 ID, 비밀번호)</li>
     * </ul>
     * 
     * <p>
     * 응답:
     * <ul>
     * <li>Status: 200 OK</li>
     * <li>Body: AuthDto.TokenResponse (액세스 토큰, 토큰 타입)</li>
     * </ul>
     * 
     * <p>
     * 인증 실패:
     * <ul>
     * <li>사용자가 존재하지 않거나 비밀번호가 일치하지 않으면 예외가 발생합니다.</li>
     * <li>현재는 500 Internal Server Error로 반환됩니다 (예외 처리 필요).</li>
     * </ul>
     * 
     * @param request 로그인 요청 DTO
     * @return JWT 토큰을 포함한 ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(@RequestBody @Valid AuthDto.LoginRequest request) {
        AuthDto.TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<AuthDto.TokenResponse> reissue(@RequestBody AuthDto.TokenRequest request) {
        return ResponseEntity.ok(authService.reissue(request));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        authService.logout(accessToken);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}
