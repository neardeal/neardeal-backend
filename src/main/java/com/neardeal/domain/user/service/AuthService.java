package com.neardeal.domain.user.service;

import com.neardeal.common.exception.CustomException;
import com.neardeal.common.exception.ErrorCode;
import com.neardeal.domain.organization.entity.University;
import com.neardeal.domain.organization.repository.UniversityRepository;
import com.neardeal.domain.user.dto.AuthTokens;
import com.neardeal.domain.user.dto.CompleteSocialSignupRequest;
import com.neardeal.domain.user.dto.LoginRequest;
import com.neardeal.domain.user.dto.SignupRequest;
import com.neardeal.domain.user.entity.*;
import com.neardeal.domain.user.repository.CustomerProfileRepository;
import com.neardeal.domain.user.repository.OwnerProfileRepository;
import com.neardeal.domain.user.repository.UserRepository;
import com.neardeal.security.details.PrincipalDetails;
import com.neardeal.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final OwnerProfileRepository ownerProfileRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final UniversityRepository universityRepository;

    @Transactional
    public Long signUp(SignupRequest request) {

        if (userRepository.existsByUsername(request.getEmail())) {
            log.warn("[SignUp] Duplicate email attempt: {}", request.getEmail());
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 이메일 입니다.");
        }

        User user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .socialType(SocialType.LOCAL)
                .build();

        Long id = userRepository.save(user).getId();

        // 역할에 따른 프로필 저장
        if (request.getRole() == Role.ROLE_CUSTOMER) {
            createCustomerProfile(user, request.getUniversityId());
        } else if (request.getRole() == Role.ROLE_OWNER) {
            createOwnerProfile(user, request.getBusinessNumber());
        }

        log.info("[SignUp] User created successfully. userId={}, email={}, role={}", id, user.getEmail(),
                user.getRole());
        return id;
    }

    @Transactional
    public AuthTokens login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser();

            log.info("[Login] Success. userId={}, email={}", user.getId(), user.getEmail());
            return generateTokenResponse(user);
        } catch (BadCredentialsException e) {
            log.warn("[Login] Failed. Invalid credentials for username: {}", request.getUsername());
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        // 토큰 유효성 검사
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("[RefreshToken] Invalid token provided.");
            throw new CustomException(ErrorCode.INVALID_TOKEN, "유효하지 않은 리프레시 토큰입니다.");
        }

        // 토큰에서 UserId 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis 비교
        String storedToken = refreshTokenService.getByUserId(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            log.warn("[RefreshToken] Token mismatch or not found in Redis. userId={}", userId);
            refreshTokenService.delete(userId);
            throw new CustomException(ErrorCode.INVALID_TOKEN, "리프레시 토큰이 만료되었거나 일치하지 않습니다.");
        }

        // 유저 조회 (유일한 DB 조회)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 토큰 재발급 (Rotation)
        log.info("[RefreshToken] Success. Tokens rotated for userId={}", userId);
        return generateTokenResponse(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            refreshTokenService.delete(userId);
        }
    }

    @Transactional
    public AuthTokens completeSocialSignup(Long userId, CompleteSocialSignupRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ROLE_GUEST) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "이미 가입이 완료된 회원입니다.");
        }

        user.completeInsufficientInfo(request.getRole(), request.getPhoneNumber());

        if (request.getRole() == Role.ROLE_CUSTOMER) {
            createCustomerProfile(user, request.getUniversityId());
        } else if (request.getRole() == Role.ROLE_OWNER) {
            createOwnerProfile(user, request.getBusinessNumber());
        }

        // 변경된 Role로 토큰 재발급
        return generateTokenResponse(user);
    }

    private void createCustomerProfile(User user, Long universityId) {
        if (universityId != null) {
            University university = universityRepository.findById(universityId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 대학을 찾을 수 없습니다."));
            CustomerProfile profile = CustomerProfile.builder().user(user).university(university).build();
            customerProfileRepository.save(profile);
        } else {
            // 대학생 아닌 고객 가입
            CustomerProfile profile = CustomerProfile.builder().user(user).build();
            customerProfileRepository.save(profile);
        }

    }

    private void createOwnerProfile(User user, String businessNumber) {
        OwnerProfile profile = OwnerProfile.builder().user(user).businessNumber(businessNumber).build();
        ownerProfileRepository.save(profile);
    }

    private AuthTokens generateTokenResponse(User user) {

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(), user.getUsername(), user.getRole().name());

        String refreshToken = jwtTokenProvider.createRefreshToken(
                user.getId(), user.getUsername(), user.getRole().name());

        refreshTokenService.save(user.getId(), refreshToken);

        return new AuthTokens(accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiresIn());
    }
}