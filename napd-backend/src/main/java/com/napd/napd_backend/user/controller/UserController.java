package com.napd.napd_backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    /*
    * 인증 테스트 API (보호된 리소스)
    * GET /api/user/profile
    * * JWT 토큰이 유효할 경우에만 접근 가능합니다.
    * */
    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile(){

        // SecurityContextHolder에서 현재 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Principal (여기서는 User ID 문자열)을 가져옵니다.
        String userId = (String) authentication.getPrincipal();

        // 인증 성공 메시지와 사용자 ID 반환
        return ResponseEntity.ok("인증 성공! 현재 로그인된 사용자 ID : " + userId);
    }
}
