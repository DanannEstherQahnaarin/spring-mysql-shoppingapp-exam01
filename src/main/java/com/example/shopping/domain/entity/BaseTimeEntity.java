package com.example.shopping.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA Auditing을 활용한 공통 시간 필드 추상 클래스
 * 
 * <p>모든 엔티티에서 공통으로 사용되는 생성 시간과 수정 시간을 자동으로 관리하기 위한
 * 베이스 엔티티 클래스입니다. 이 클래스를 상속받는 엔티티는 자동으로 createdAt과 updatedAt
 * 필드를 가지게 됩니다.
 * 
 * <p>사용 방법:
 * <pre>
 * &#64;Entity
 * public class User extends BaseTimeEntity {
 *     // createdAt, updatedAt 필드가 자동으로 추가됨
 * }
 * </pre>
 * 
 * <p>주의사항:
 * <ul>
 *   <li>JPA Auditing 기능을 사용하기 위해 메인 애플리케이션 클래스에
 *       &#64;EnableJpaAuditing 어노테이션이 필요합니다.</li>
 *   <li>createdAt은 생성 시점에 자동으로 설정되며 이후 수정되지 않습니다.</li>
 *   <li>updatedAt은 엔티티가 저장되거나 수정될 때마다 자동으로 갱신됩니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    /**
     * 엔티티 생성 시간
     * 엔티티가 처음 저장될 때 자동으로 현재 시간이 설정되며, 이후 변경되지 않습니다.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 엔티티 수정 시간
     * 엔티티가 저장되거나 수정될 때마다 자동으로 현재 시간으로 갱신됩니다.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
