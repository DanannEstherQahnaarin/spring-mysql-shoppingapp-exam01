package com.example.shopping.domain.entity.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * 역할(Role) 엔티티
 * 
 * <p>시스템에서 사용되는 사용자 역할을 정의하는 엔티티입니다.
 * Spring Security의 역할 기반 접근 제어(RBAC)에 활용됩니다.
 * 
 * <p>사용 목적:
 * <ul>
 *   <li>사용자의 권한을 그룹화하여 관리합니다.</li>
 *   <li>ROLE_USER, ROLE_ADMIN 등의 역할 코드를 정의합니다.</li>
 *   <li>역할별로 접근 가능한 리소스를 제어할 수 있습니다.</li>
 * </ul>
 * 
 * <p>주의사항:
 * <ul>
 *   <li>현재 버전에서는 User와의 관계 매핑이 구현되지 않았습니다.</li>
 *   <li>향후 User와 Many-to-Many 관계를 맺어 사용자-역할 매핑을 구현할 수 있습니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    /**
     * 역할 고유 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 역할 코드
     * Spring Security에서 사용되는 역할 코드입니다.
     * 예: "ROLE_USER", "ROLE_ADMIN", "ROLE_SELLER" 등
     * 고유해야 하며, 최대 50자까지 입력 가능합니다.
     */
    @Column(name = "role_code", unique = true, length = 50)
    private String roleCode;

    /**
     * 역할 이름
     * 역할을 사용자에게 표시할 때 사용되는 이름입니다.
     * 예: "일반 사용자", "관리자", "판매자" 등
     * 최대 100자까지 입력 가능합니다.
     */
    @Column(name = "role_name", length = 100)
    private String roleName;

    /**
     * 역할 유형
     * 역할을 분류하기 위한 유형 정보입니다.
     * 예: "SYSTEM", "CUSTOM" 등
     * 최대 20자까지 입력 가능합니다.
     */
    @Column(name = "role_type", length = 20)
    private String roleType;
}