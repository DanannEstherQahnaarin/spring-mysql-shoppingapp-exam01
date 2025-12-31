package com.example.shopping.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDto {
    @Data
    public static class SignupRequest {
        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
        @NotBlank
        private String name;
        private String email;
        private String phone;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String accessToken;
        private String tokenType = "Bearer";
        
        public TokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
