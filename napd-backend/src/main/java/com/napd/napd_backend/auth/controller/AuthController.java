package com.napd.napd_backend.auth.controller;

import com.napd.napd_backend.auth.dto.LoginRequestDto;
import com.napd.napd_backend.auth.dto.SignUpRequestDto;
import com.napd.napd_backend.auth.dto.TokenResponseDto;
import com.napd.napd_backend.auth.service.AuthService;
import com.napd.napd_backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")    // 기본 경로 설정
@RequiredArgsConstructor
public class AuthController {
    private  final AuthService authService;
    /*
    * 회원가입 API
    * POST /api/auth/signup
    */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto){
        try {
            User user = authService.signUp(requestDto);
            // 성공 시 201 Created 응답
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다. 사용자 ID: "+user.getId());

        } catch (IllegalArgumentException e){
            // 중복 등의 비즈니스 로직 오류 시 400 Bad Request 응답
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            // 그 외 서버 오류 시 500 Internal Server Error 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 서버 오류가 발생했습니다.");
        }
    }

    /*
    * 로그인 API
    * POST /api/auth/login
    */
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
        try{
            TokenResponseDto tokens = authService.login(requestDto);

            // 성공 시 200 OK와 토큰 정보 반환
            return ResponseEntity.ok(tokens);
        }catch (IllegalArgumentException e) {
            // 이메일/비밀번호 불일치 시 400 Bad Request
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            // 서버 오류 시 500 Internal Server Error
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 서버 오류가 발생했습니다.");
        }
    }
}
