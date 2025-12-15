package com.napd.napd_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String grantType = "Bearer"; // 토큰 타입(Bearear 방식)
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn; // 만료 시간 (밀리초)
}
