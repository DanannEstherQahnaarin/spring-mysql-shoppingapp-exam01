package com.example.shopping.domain.entity.user;

import java.time.LocalDateTime;

import com.example.shopping.domain.entity.BaseTimeEntity;
import com.example.shopping.domain.enums.UserStatus;
import com.example.shopping.domain.enums.JoinType;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * 
 * <p>쇼핑몰 애플리케이션의 사용자 정보를 관리하는 핵심 엔티티입니다.
 * 사용자의 기본 정보(로그인 ID, 이메일, 전화번호)와 상태, 가입 유형을 저장합니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>UserAuth와 1:1 관계 (인증 정보)</li>
 *   <li>UserProfile과 1:1 관계 (프로필 정보)</li>
 * </ul>
 * 
 * <p>인덱스:
 * <ul>
 *   <li>email 컬럼에 인덱스가 설정되어 있어 이메일 기반 조회 성능이 최적화되어 있습니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users", columnList = "email")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class User extends BaseTimeEntity {

    /**
     * 사용자 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * 로그인 ID
     * 사용자가 로그인할 때 사용하는 고유한 식별자입니다.
     * 중복이 허용되지 않으며, 최대 50자까지 입력 가능합니다.
     */
    @Column(name = "login_id", unique = true, nullable = false, length = 50)
    private String loginId;

    /**
     * 이메일 주소
     * 사용자의 이메일 주소로, 고유해야 합니다.
     * 최대 100자까지 입력 가능하며, 이메일 기반 조회에 인덱스가 설정되어 있습니다.
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 전화번호
     * 사용자의 전화번호로, 고유해야 합니다.
     * 최대 20자까지 입력 가능합니다.
     */
    @Column(unique = true, length = 20)
    private String phone;

    /**
     * 사용자 상태
     * 사용자의 현재 상태를 나타내는 열거형 값입니다.
     * ACTIVE(활성), SUSPENDED(정지), WITHDRAWN(탈퇴), DORMANT(휴면) 등이 있습니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    /**
     * 가입 유형
     * 사용자가 가입한 방식을 나타내는 열거형 값입니다.
     * LOCAL(로컬 가입), KAKAO(카카오), NAVER(네이버), GOOGLE(구글), APPLE(애플) 등이 있습니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "join_type", nullable = false, length = 20)
    private JoinType joinType;

    /**
     * 마지막 로그인 시간
     * 사용자가 마지막으로 로그인한 시간을 저장합니다.
     * 로그인 성공 시 updateLastLogin() 메서드를 통해 갱신됩니다.
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    /**
     * 마지막 로그인 시간을 현재 시간으로 갱신합니다.
     * 
     * <p>사용자가 로그인에 성공했을 때 호출되어야 합니다.
     * 이 메서드를 호출하면 lastLoginAt 필드가 현재 시간으로 업데이트됩니다.
     * 
     * <p>사용 예:
     * <pre>
     * user.updateLastLogin();
     * userRepository.save(user);
     * </pre>
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
