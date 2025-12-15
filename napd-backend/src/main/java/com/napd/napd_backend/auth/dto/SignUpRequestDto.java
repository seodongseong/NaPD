package com.napd.napd_backend.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    // 유효성 검사 어노테이션은 나중에 추가
    private String email;
    private String password;
    private String nickname;
}
