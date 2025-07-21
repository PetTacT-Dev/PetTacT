package com.pettact.api.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pettact.api.security.service.CustomUserDetailsService;
import com.pettact.api.security.util.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDED_PATHS = List.of(
        "/v1/user/login",
        "/v1/user/join",
        "/v1/user/email/**",
        "/v1/user/nickname/check",
        "/v1/user/password/**",
        "/refreshToken",
        "/login",
        "/oauth2/**",
        "/login/oauth2/**",
        "/favicon.ico",
        "/default-ui.css",
        "/v1/api/abandonment/**",
        "/v1/pet/abandonment/**",
        "/v1/notification/subscribe",
        "/v1/multifile/image/**",
        "/v1/product",
        "/v1/product/popular",
        "/v1/payments/confirm",
        "/ws-stomp", // sockjs 경로도 리스트에 포함
        
        // Swagger 경로 예외 처리
        "/swagger-ui.html", "/swagger-ui/**",
        "/v3/api-docs", "/v3/api-docs/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("요청 URI: {}", path);

        // JWT 검사 제외 경로 처리
        if (EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            log.info("TokenCheckFilter skip (예외 경로): {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            setAuthentication(request);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("JWT 검증 실패: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void setAuthentication(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        log.info("🔑 Authorization Header: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization 헤더가 없습니다.");
        }

        String token = header.substring(7); // "Bearer " 제거
        Map<String, Object> claims = jwtTokenProvider.validateToken(token);
        String email = (String) claims.get("userEmail");

        log.info("Token claims: {}", claims);
        log.info("인증된 이메일: {}", email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
