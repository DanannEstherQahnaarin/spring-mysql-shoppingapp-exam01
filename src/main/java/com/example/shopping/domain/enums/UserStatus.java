package com.example.shopping.domain.enums;

/**
 * 사용자 상태 열거형
 * 
 * <p>시스템에서 사용자의 현재 상태를 나타내는 열거형입니다.
 * User 엔티티의 status 필드에서 사용됩니다.
 * 
 * <p>상태 값:
 * <ul>
 *   <li>ACTIVE: 활성 상태 - 정상적으로 서비스를 이용할 수 있는 사용자</li>
 *   <li>SUSPENDED: 정지 상태 - 관리자에 의해 일시적으로 이용이 정지된 사용자</li>
 *   <li>WITHDRAWN: 탈퇴 상태 - 사용자가 계정을 탈퇴한 상태</li>
 *   <li>DORMANT: 휴면 상태 - 장기간 미접속으로 인해 휴면 처리된 사용자</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * User user = User.builder()
 *     .status(UserStatus.ACTIVE)
 *     .build();
 * </pre>
 * 
 * @author shopping-server
 * @since 1.0
 */
public enum UserStatus {
    /** 활성 상태 - 정상적으로 서비스를 이용할 수 있는 사용자 */
    ACTIVE,
    
    /** 정지 상태 - 관리자에 의해 일시적으로 이용이 정지된 사용자 */
    SUSPENDED,
    
    /** 탈퇴 상태 - 사용자가 계정을 탈퇴한 상태 */
    WITHDRAWN,
    
    /** 휴면 상태 - 장기간 미접속으로 인해 휴면 처리된 사용자 */
    DORMANT
}
