package com.example.shopping.domain.entity.user;

import java.time.LocalDateTime;

import com.example.shopping.domain.entity.BaseTimeEntity;
import com.example.shopping.domain.enums.UserStatus;
import com.example.shopping.domain.enums.JoinType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users", columnList = "email")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", unique = true, nullable = false, length = 50)
    private String loginId;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_type", nullable = false, length = 20)
    private JoinType joinType;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // 로그인 성공 시 호출할 메서드
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
