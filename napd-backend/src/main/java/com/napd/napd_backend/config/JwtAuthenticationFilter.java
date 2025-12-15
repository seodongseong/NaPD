package com.napd.napd_backend.config;

import com.napd.napd_backend.auth.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.authenticator.jaspic.SimpleAuthConfigProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)){
            // 3. 토큰이 유효하면 인증 객체(Authentication) 생성
            //      (이 로직은 다음 단계에서 UserDetailsService 구현 후 구체화 됩니다.)
            //      일단 지금은 Security Context에 인증 정보를 넣는 로직만 임시로 작성합니다
            String subject = jwtTokenProvider.getClaims(token).getSubject();

            // 임시 Authentication 객체 생성 ( 실제 구현 시 UserDetailsService 필요)
            Authentication authentication = new SimpleAuthentication(subject);

            // 4. SecurityContext에 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출 (beare <token> 형식)
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // "bearer " 이후의 토큰 문자열 반환
        }
        return null;
    }


}


class SimpleAuthentication implements Authentication {
    private final String principal;
    private boolean authenticated = true;

    public SimpleAuthentication(String principal){
        this.principal = principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
    @Override
    public Object getCredentials() { return null; }
    @Override
    public Object getDetails() { return null; }
    @Override
    public Object getPrincipal() { return this.principal; }
    @Override
    public boolean isAuthenticated() { return authenticated; }
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException { this.authenticated = isAuthenticated; }
    @Override
    public String getName() { return this.principal; }
}

