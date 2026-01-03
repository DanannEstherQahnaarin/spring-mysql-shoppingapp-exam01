package com.example.shopping.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.domain.dto.UserDto;
import com.example.shopping.domain.service.UserService;
import com.example.shopping.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    private Long getUserId(String token) {
        return Long.parseLong(jwtTokenProvider.getUserPk(token.substring(7)));
    }

    // 프로필 수정
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String token, @RequestBody UserDto.UpdateProfile request) {
        userService.updateProfile(getUserId(token), request);
        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }

    // 비밀번호 변경 (로그인 중)
    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @RequestBody UserDto.ChangePassword request) {
        userService.changePassword(getUserId(token), request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<String> withdraw(@RequestHeader("Authorization") String token) {
        userService.withdraw(getUserId(token));
        return ResponseEntity.ok("탈퇴 처리되었습니다.");
    }

    // --- 비밀번호 찾기 (로그인 불필요) ---

    // 인증코드 발송
    @PostMapping("/password/recovery/send")
    public ResponseEntity<String> sendRecoveryCode(@RequestBody UserDto.FindPassword request) {
        userService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증코드가 메일로 발송되었습니다.");
    }

    // 비밀번호 초기화
    @PostMapping("/password/recovery/reset")
    public ResponseEntity<String> resetPassword(@RequestBody UserDto.ResetPassword request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 초기화되었습니다. 로그인해주세요.");
    }
}