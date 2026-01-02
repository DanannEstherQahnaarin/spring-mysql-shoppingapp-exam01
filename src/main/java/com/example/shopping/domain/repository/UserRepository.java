package com.example.shopping.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopping.domain.entity.user.User;

/**
 * 사용자 Repository 인터페이스
 * 
 * <p>User 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본 CRUD 메서드를 제공하며,
 * 메서드 이름 기반 쿼리 생성 기능을 활용합니다.
 * 
 * <p>제공 메서드:
 * <ul>
 *   <li>JpaRepository 기본 메서드: save, findById, findAll, delete 등</li>
 *   <li>findByLoginId: 로그인 ID로 사용자 조회</li>
 *   <li>findByEmail: 이메일로 사용자 조회</li>
 *   <li>existsByLoginId: 로그인 ID 중복 확인</li>
 *   <li>existsByEmail: 이메일 중복 확인</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * Optional&lt;User&gt; user = userRepository.findByLoginId("user123");
 * boolean exists = userRepository.existsByLoginId("user123");
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public interface UserRepository extends JpaRepository<User, Long>, UserCusomRepository {
    /**
     * 로그인 ID로 사용자를 조회합니다.
     * 
     * @param loginId 조회할 사용자의 로그인 ID
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findByLoginId(String loginId);
    
    /**
     * 이메일로 사용자를 조회합니다.
     * 
     * @param email 조회할 사용자의 이메일 주소
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 로그인 ID의 중복 여부를 확인합니다.
     * 
     * @param loginId 확인할 로그인 ID
     * @return 중복이면 true, 아니면 false
     */
    boolean existsByLoginId(String loginId);
    
    /**
     * 이메일의 중복 여부를 확인합니다.
     * 
     * @param email 확인할 이메일 주소
     * @return 중복이면 true, 아니면 false
     */
    boolean existsByEmail(String email);
}
