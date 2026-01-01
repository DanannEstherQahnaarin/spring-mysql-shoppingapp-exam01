package com.example.shopping.domain.enums;

/**
 * 가입 유형 열거형
 * 
 * <p>사용자가 회원가입할 때 사용한 인증 방식을 나타내는 열거형입니다.
 * User 엔티티의 joinType 필드에서 사용됩니다.
 * 
 * <p>가입 유형:
 * <ul>
 *   <li>LOCAL: 로컬 가입 - 이메일/비밀번호로 직접 가입한 사용자</li>
 *   <li>KAKAO: 카카오 소셜 로그인을 통해 가입한 사용자</li>
 *   <li>NAVER: 네이버 소셜 로그인을 통해 가입한 사용자</li>
 *   <li>GOOGLE: 구글 소셜 로그인을 통해 가입한 사용자</li>
 *   <li>APPLE: 애플 소셜 로그인을 통해 가입한 사용자</li>
 * </ul>
 * 
 * <p>사용 예:
 * <pre>
 * User user = User.builder()
 *     .joinType(JoinType.LOCAL)
 *     .build();
 * </pre>
 * 
 * <p>비즈니스 로직:
 * <ul>
 *   <li>소셜 로그인 사용자의 경우 비밀번호 변경 기능을 제한할 수 있습니다.</li>
 *   <li>가입 유형에 따라 다른 인증 플로우를 적용할 수 있습니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
public enum JoinType {
    /** 로컬 가입 - 이메일/비밀번호로 직접 가입한 사용자 */
    LOCAL,
    
    /** 카카오 소셜 로그인을 통해 가입한 사용자 */
    KAKAO,
    
    /** 네이버 소셜 로그인을 통해 가입한 사용자 */
    NAVER,
    
    /** 구글 소셜 로그인을 통해 가입한 사용자 */
    GOOGLE,
    
    /** 애플 소셜 로그인을 통해 가입한 사용자 */
    APPLE
}
