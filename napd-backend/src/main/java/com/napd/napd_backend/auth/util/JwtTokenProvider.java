package com.napd.napd_backend.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long ACCESS_TOKEN_EXPIRE_TIME; // 1시간
    private final long REFRESH_TOKEN_EXPIRE_TIME; // 7일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        // application.yml에 정의될 Secret Key를 Base64 디코딩하여 key 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        // 개발 편의상 짧게 설정
        this.ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간
        this.REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    }

    // Access Token 생성
    public String generateAccessToken(String subject) {
        return generateToken(subject, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // Refresh Token 생성 (향후 토큰 재발급에 사용)
    public String generateRefreshToken(String subject) {
        return generateToken(subject, REFRESH_TOKEN_EXPIRE_TIME);
    }

    // JWT 토큰 생성 핵심 로직
    private String generateToken(String subject, long expirationTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(subject) // 토큰 주체 (보통 사용자 ID 또는 이메일)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e){
            // 토큰 파싱 실패(만료, 변조 등)
            return false;
        }
    }

    // 토큰에서 Claim 정보 추출 (주체, 만료 시간 등)
    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}
