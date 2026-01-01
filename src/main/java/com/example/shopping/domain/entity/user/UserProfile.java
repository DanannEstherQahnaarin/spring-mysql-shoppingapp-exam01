package com.example.shopping.domain.entity.user;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 프로필 엔티티
 * 
 * <p>사용자의 추가 프로필 정보를 저장하는 엔티티입니다.
 * 이름, 생년월일, 성별 등 사용자의 개인정보를 관리합니다.
 * 
 * <p>관계:
 * <ul>
 *   <li>User와 1:1 식별 관계를 가지며, User의 PK(userId)를 자신의 PK이자 FK로 사용합니다.</li>
 *   <li>@MapsId 어노테이션을 통해 식별 관계를 구현합니다.</li>
 * </ul>
 * 
 * <p>설계 목적:
 * <ul>
 *   <li>User 엔티티와 분리하여 프로필 정보를 별도로 관리함으로써 데이터 정규화를 실현합니다.</li>
 *   <li>프로필 정보가 선택적(Optional)인 경우를 고려한 설계입니다.</li>
 *   <li>LAZY 로딩을 통해 필요한 경우에만 프로필 정보를 조회합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile {
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
     * 사용자 이름
     * 사용자의 실명 또는 닉네임을 저장합니다.
     * 필수 입력 항목이며, 최대 50자까지 입력 가능합니다.
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 생년월일
     * 사용자의 생년월일을 저장합니다.
     * 선택적 입력 항목이며, LocalDate 타입으로 날짜만 저장합니다.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * 성별
     * 사용자의 성별을 저장합니다.
     * 선택적 입력 항목이며, 1자리 문자로 저장합니다 (예: 'M', 'F').
     */
    @Column(length = 1)
    private String gender;
}
