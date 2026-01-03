package com.example.shopping.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class UserDto {

    @Data
    public static class ChangePassword {
        @NotBlank
        private String currentPassword;
        @NotBlank
        private String newPassword;
    }

    @Data
    public static class UpdateProfile {
        private String name;
        private String phone;
        // 필요한 경우 주소 정보 등 추가
    }

    @Data
    public static class FindPassword {
        @NotBlank
        private String email;
    }

    @Data
    public static class ResetPassword {
        @NotBlank
        private String email;
        @NotBlank
        private String verificationCode;
        @NotBlank
        private String newPassword;
    }
}