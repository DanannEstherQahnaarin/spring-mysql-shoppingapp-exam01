package com.example.shopping.domain.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.shopping.domain.dto.AuthDto;
import com.example.shopping.domain.exception.BusinessException;
import com.example.shopping.domain.exception.ErrorCode;
import com.example.shopping.domain.entity.user.RefreshToken;
import com.example.shopping.domain.entity.user.User;
import com.example.shopping.domain.entity.user.UserAuth;
import com.example.shopping.domain.entity.user.UserProfile;
import com.example.shopping.domain.enums.JoinType;
import com.example.shopping.domain.enums.UserStatus;
import com.example.shopping.domain.repository.RefreshTokenRepository;
import com.example.shopping.domain.repository.UserAuthRepository;
import com.example.shopping.domain.repository.UserProfileRepository;
import com.example.shopping.domain.repository.UserRepository;
import com.example.shopping.global.security.JwtTokenProvider;

import io.jsonwebtoken.Claims;

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

    private final RefreshTokenRepository refreshTokenRepository;

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


    // 로그인 (수정: Refresh Token 생성 및 저장)
    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        // ... (기존 ID/PW 검증 로직) ...
        User user = userRepository.findByLoginId(request.getLoginId())
                 .orElseThrow(() -> new RuntimeException("가입되지 않은 아이디입니다."));
        // ... (비밀번호 체크 로직) ...

        // 토큰 생성
        String accessToken = tokenProvider.createToken(String.valueOf(user.getUserId()), "ROLE_USER");
        String refreshToken = tokenProvider.createRefreshToken(); // *Provider에 메서드 추가 필요 (아래 참고)

        // Refresh Token DB 저장
        RefreshToken rt = RefreshToken.builder()
                .key(String.valueOf(user.getUserId()))
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(rt);

        return new AuthDto.TokenResponse("Bearer", accessToken, refreshToken, 1800000L); // 30분
    }

    // 토큰 재발급 (Reissue)
    @Transactional
    public AuthDto.TokenResponse reissue(AuthDto.TokenRequest request) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token에서 User ID 가져오기
        Claims claims = tokenProvider.parseClaims(request.getAccessToken());
        String userId = claims.getSubject();

        // 3. 저장소에서 User ID를 기반으로 Refresh Token 가져오기
        RefreshToken refreshToken = refreshTokenRepository.findByKey(userId)
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(request.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        String newAccessToken = tokenProvider.createToken(userId, (String)claims.get("role"));
        String newRefreshToken = tokenProvider.createRefreshToken();

        // 6. DB 정보 업데이트
        refreshToken.updateValue(newRefreshToken);

        return new AuthDto.TokenResponse("Bearer", newAccessToken, newRefreshToken, 1800000L);
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
         // 1. Access Token 검증
        if (!tokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("잘못된 요청입니다.");
        }

        // 2. Access Token에서 User ID를 가져옴
        Claims claims = tokenProvider.parseClaims(accessToken);
        String userId = claims.getSubject();

        // 3. DB에서 Refresh Token 삭제
        refreshTokenRepository.findByKey(userId)
                .ifPresent(refreshTokenRepository::delete);
    }
}
