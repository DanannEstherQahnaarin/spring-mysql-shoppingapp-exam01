package com.example.shopping.domain.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.shopping.domain.dto.AuthDto;
import com.example.shopping.domain.exception.BusinessException;
import com.example.shopping.domain.exception.ErrorCode;
import com.example.shopping.domain.entity.user.User;
import com.example.shopping.domain.entity.user.UserAuth;
import com.example.shopping.domain.entity.user.UserProfile;
import com.example.shopping.domain.enums.JoinType;
import com.example.shopping.domain.enums.UserStatus;
import com.example.shopping.domain.repository.UserAuthRepository;
import com.example.shopping.domain.repository.UserProfileRepository;
import com.example.shopping.domain.repository.UserRepository;
import com.example.shopping.global.security.JwtTokenProvider;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 * 인증 서비스 클래스
 * 
 * <p>사용자 인증과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 회원가입, 로그인 등의 기능을 제공합니다.
 * 
 * <p>주요 기능:
 * <ul>
 *   <li>회원가입: 사용자 정보, 프로필, 인증 정보를 함께 저장</li>
 *   <li>로그인: 사용자 인증 후 JWT 토큰 발급</li>
 * </ul>
 * 
 * <p>트랜잭션 관리:
 * <ul>
 *   <li>모든 메서드는 @Transactional로 트랜잭션을 관리합니다.</li>
 *   <li>회원가입 시 User, UserProfile, UserAuth를 함께 저장하므로 원자성이 보장됩니다.</li>
 * </ul>
 * 
 * <p>보안:
 * <ul>
 *   <li>비밀번호는 PasswordEncoder를 사용하여 해시화하여 저장합니다.</li>
 *   <li>로그인 시 비밀번호는 평문과 해시값을 비교하여 검증합니다.</li>
 *   <li>JWT 토큰을 사용하여 사용자 인증을 관리합니다.</li>
 * </ul>
 * 
 * @author shopping-server
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    /** 사용자 Repository */
    private final UserRepository userRepository;
    
    /** 사용자 인증 정보 Repository */
    private final UserAuthRepository userAuthRepository;
    
    /** 사용자 프로필 Repository */
    private final UserProfileRepository userProfileRepository;
    
    /** 비밀번호 암호화 인코더 */
    private final PasswordEncoder passwordEncoder;
    
    /** JWT 토큰 제공자 */
    private final JwtTokenProvider tokenProvider;

    /**
     * 회원가입을 처리합니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>로그인 ID와 이메일의 중복 여부를 확인합니다 (현재는 예외 처리 미구현).</li>
     *   <li>User 엔티티를 생성하고 저장합니다.</li>
     *   <li>UserProfile 엔티티를 생성하고 저장합니다.</li>
     *   <li>비밀번호를 해시화하여 UserAuth 엔티티를 생성하고 저장합니다.</li>
     * </ol>
     * 
     * <p>트랜잭션:
     * <ul>
     *   <li>모든 저장 작업이 하나의 트랜잭션으로 처리됩니다.</li>
     *   <li>어떤 단계에서든 예외가 발생하면 전체가 롤백됩니다.</li>
     * </ul>
     * 
     * @param request 회원가입 요청 DTO (로그인 ID, 비밀번호, 이름, 이메일, 전화번호)
     * @return 생성된 사용자의 ID
     * @throws RuntimeException 로그인 ID 또는 이메일이 중복되는 경우 (현재 예외 처리 미구현)
     */
    @Transactional
    public Long signUp(AuthDto.SignupRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .loginId(request.getLoginId())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .joinType(JoinType.LOCAL)
                .build();

        User saveUser = userRepository.save(user);

        UserProfile userProfile = UserProfile.builder()
                .user(saveUser)
                .name(request.getName())
                .build();

        userProfileRepository.save(userProfile);

        UserAuth userAuth = UserAuth.builder()
                .user(saveUser)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userAuthRepository.save(userAuth);

        return saveUser.getUserId();
    }

    /**
     * 로그인을 처리하고 JWT 토큰을 발급합니다.
     * 
     * <p>처리 과정:
     * <ol>
     *   <li>로그인 ID로 사용자를 조회합니다.</li>
     *   <li>사용자의 인증 정보를 조회합니다.</li>
     *   <li>입력된 비밀번호와 저장된 해시값을 비교하여 인증합니다.</li>
     *   <li>인증 성공 시 마지막 로그인 시간을 업데이트합니다.</li>
     *   <li>JWT 토큰을 생성하여 반환합니다.</li>
     * </ol>
     * 
     * <p>트랜잭션:
     * <ul>
     *   <li>로그인 시간 업데이트와 토큰 발급이 하나의 트랜잭션으로 처리됩니다.</li>
     * </ul>
     * 
     * <p>인증 실패:
     * <ul>
     *   <li>사용자가 존재하지 않는 경우: Optional.orElseThrow()로 예외 발생</li>
     *   <li>비밀번호가 일치하지 않는 경우: RuntimeException 발생 (현재 예외 처리 미구현)</li>
     * </ul>
     * 
     * @param request 로그인 요청 DTO (로그인 ID, 비밀번호)
     * @return JWT 토큰이 포함된 응답 DTO
     * @throws RuntimeException 사용자가 존재하지 않거나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        UserAuth uAuth = userAuthRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), uAuth.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.updateLastLogin();
        String token = tokenProvider.createToken(String.valueOf(user.getUserId()), "ROLE_USER");
        return new AuthDto.TokenResponse(token);
    }
}
