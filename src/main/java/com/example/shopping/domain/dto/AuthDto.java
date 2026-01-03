package com.example.shopping.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 인증 관련 DTO 클래스
 * 
 * <p>사용자 인증과 관련된 요청 및 응답 데이터를 전송하기 위한 DTO 클래스들을
 * 포함하는 외부 클래스입니다. 내부 정적 클래스로 요청/응답 DTO를 정의합니다.
 * 
 * <p>사용 목적:
 * <ul>
 *   <li>Controller와 Service 계층 간 데이터 전송</li>
 *   <li>API 요청/응답 스펙 정의</li>
 *   <li>데이터 검증 (Jakarta Validation)</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
public class AuthDto {
    
    /**
     * 회원가입 요청 DTO
     * 
     * <p>사용자 회원가입 시 필요한 정보를 담는 요청 객체입니다.
     * 
     * <p>검증 규칙:
     * <ul>
     *   <li>loginId: 필수 입력 (NotBlank)</li>
     *   <li>password: 필수 입력 (NotBlank)</li>
     *   <li>name: 필수 입력 (NotBlank)</li>
     *   <li>email: 선택 입력</li>
     *   <li>phone: 선택 입력</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * AuthDto.SignupRequest request = new AuthDto.SignupRequest();
     * request.setLoginId("user123");
     * request.setPassword("password123");
     * request.setName("홍길동");
     * </pre>
     */
    @Data
    public static class SignupRequest {
        /** 로그인 ID - 사용자가 로그인할 때 사용하는 고유한 식별자 */
        @NotBlank
        private String loginId;
        
        /** 비밀번호 - 사용자의 비밀번호 (해시화되어 저장됨) */
        @NotBlank
        private String password;
        
        /** 사용자 이름 - 사용자의 실명 또는 닉네임 */
        @NotBlank
        private String name;
        
        /** 이메일 주소 - 선택 입력 항목 */
        private String email;
        
        /** 전화번호 - 선택 입력 항목 */
        private String phone;
    }

    /**
     * 로그인 요청 DTO
     * 
     * <p>사용자 로그인 시 필요한 정보를 담는 요청 객체입니다.
     * 
     * <p>검증 규칙:
     * <ul>
     *   <li>loginId: 필수 입력 (NotBlank)</li>
     *   <li>password: 필수 입력 (NotBlank)</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * AuthDto.LoginRequest request = new AuthDto.LoginRequest();
     * request.setLoginId("user123");
     * request.setPassword("password123");
     * </pre>
     */
    @Data
    public static class LoginRequest {
        /** 로그인 ID - 사용자의 로그인 ID */
        @NotBlank
        private String loginId;
        
        /** 비밀번호 - 사용자의 비밀번호 (평문) */
        @NotBlank
        private String password;
    }

    /**
     * 토큰 응답 DTO
     * 
     * <p>로그인 성공 시 JWT 토큰을 반환하는 응답 객체입니다.
     * 
     * <p>응답 구조:
     * <ul>
     *   <li>accessToken: JWT 액세스 토큰</li>
     *   <li>tokenType: 토큰 타입 (기본값: "Bearer")</li>
     * </ul>
     * 
     * <p>사용 예:
     * <pre>
     * String token = jwtTokenProvider.createToken(userId, "ROLE_USER");
     * AuthDto.TokenResponse response = new AuthDto.TokenResponse(token);
     * // response.accessToken = token
     * // response.tokenType = "Bearer"
     * </pre>
     */
    @Data
    @AllArgsConstructor
    public static class TokenResponse {
        private String grantType;
        private String accessToken;
        private String refreshToken; // 추가됨
        private Long accessTokenExpiresIn;
    }

    @Data
    public static class TokenRequest {
        private String accessToken;
        private String refreshToken;
    }
}
