package com.napd.napd_backend.auth.service;

import com.napd.napd_backend.auth.dto.LoginRequestDto;
import com.napd.napd_backend.auth.dto.SignUpRequestDto;
import com.napd.napd_backend.auth.dto.TokenResponseDto;
import com.napd.napd_backend.auth.util.JwtTokenProvider;
import com.napd.napd_backend.user.entity.*;
import com.napd.napd_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 다음 단계에서 Bean으로 등록 예정
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User signUp(SignUpRequestDto requestDto) {
        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 2. 닉네임 중복 확인
        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw  new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode((requestDto.getPassword()));

        // 4. User 객체 생성 및 저장
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(encodedPassword);
        user.setNickname(requestDto.getNickname());
        user.setRole(Role.USER); // 기본 권한 설정

        return userRepository.save(user);
    }
    // 로그인 로직 추가
    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto requestDto){

        // 1. 이메일로 사용자 조회 및 예외 처리
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 일치 확인 및 예외 처리
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성 및 반환
        String userSubject = String.valueOf(user.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(userSubject);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userSubject);

        return new TokenResponseDto("Bearer", accessToken, refreshToken, 60 * 60 * 1000L); // 1시간
    }
}
