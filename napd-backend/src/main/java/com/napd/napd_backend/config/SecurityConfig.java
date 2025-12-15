package com.napd.napd_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 필터 주입을 위해 추가
public class SecurityConfig {
    // 비밀번호 암호화에 사용할 BCryptPasswordEncoder를 Bean으로 등록합니다
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // (참고 : 실제 보안 설정은 이후 JWT와 함께 이 파일에 추가됩니다.)
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // SecurityFilterChain Bean 정의 (중요)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (REST API에서는 토큰 사용)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 회원가입 경로는 인증 없이 접근을 허용(permitAll)합니다.
                        .requestMatchers("/api/auth/signup","/api/auth/login","/api/contents").permitAll()
                        // POST, PUT, DELETE 요청은 인증 필요
                        .requestMatchers(HttpMethod.POST, "/api/contents").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/contents/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/contents/**").authenticated()

                        // 나머지 모든 요청은 인증(로그인)이 필요합니다.
                        .anyRequest().authenticated()
                );

        // JWT 필터를 usernamePasswordAuthenticationFilter 이전에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
