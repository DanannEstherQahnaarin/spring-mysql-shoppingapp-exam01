package com.example.shopping.domain.entity.user;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 인증 정보 엔티티
 * 
 * <p>사용자의 인증 관련 정보를 저장하는 엔티티입니다.
 * 비밀번호 해시값, 비밀번호 변경 시간, 로그인 실패 횟수 등을 관리합니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>User와 1:1 식별 관계를 가지며, User의 PK(userId)를 자신의 PK이자 FK로 사용합니다.</li>
 *   <li>@MapsId 어노테이션을 통해 식별 관계를 구현합니다.</li>
 * </ul>
 * 
 * <p>보안 고려사항:
 * <ul>
 *   <li>비밀번호는 평문이 아닌 해시값으로 저장됩니다.</li>
 *   <li>Spring Security의 PasswordEncoder를 사용하여 암호화해야 합니다.</li>
 *   <li>비밀번호 변경 시간을 추적하여 주기적인 비밀번호 변경을 권장할 수 있습니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "user_auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAuth {

    /**
     * 사용자 ID (Primary Key, Foreign Key)
     * User 엔티티의 userId와 동일한 값을 가지며, 식별 관계를 형성합니다.
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 사용자 엔티티 참조
     * User 테이블과 1:1 식별 관계를 맺으며, LAZY 로딩을 사용합니다.
     * @MapsId 어노테이션으로 userId가 User의 PK이자 이 엔티티의 PK가 됩니다.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 비밀번호 해시값
     * 사용자의 비밀번호를 암호화한 해시값을 저장합니다.
     * Spring Security의 BCrypt 등 암호화 알고리즘을 사용해야 합니다.
     * 최대 255자까지 저장 가능합니다.
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    /**
     * 비밀번호 변경 시간
     * 사용자가 비밀번호를 마지막으로 변경한 시간을 저장합니다.
     * 비밀번호 변경 정책(예: 90일마다 변경) 구현에 활용할 수 있습니다.
     */
    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    /**
     * 로그인 실패 횟수
     * 연속으로 로그인에 실패한 횟수를 추적합니다.
     * 기본값은 0이며, 일정 횟수 이상 실패 시 계정을 잠글 수 있습니다.
     */
    @Column(name = "failed_login_count")
    @Builder.Default
    private int failedLoginCount = 0;
}
