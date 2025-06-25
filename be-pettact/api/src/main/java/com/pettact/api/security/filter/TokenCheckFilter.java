package com.pettact.api.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    // 토큰 검사 제외할 url
    private static final List<String> EXCLUDED_PATHS = List.of(
    	    "/v1/user/login",
    	    "/v1/user/join",
    	    "/v1/user/email/send",
    	    "/v1/user/email/verify",
    	    "/v1/user/email/find",
		    "/v1/user/password/send",
		    "/v1/user/password/verify",
		    "/v1/user/password/reset",
    	    "/refreshToken",
    	    "/login",
    	    "/oauth2/",
    	    "/login/oauth2/",
    	    "/favicon.ico",
    	    "/default-ui.css",
    	    "/api/fetch",
            "/v1/board-categories",
            "/v1/board",
            "/board/1/replies",
            "/v1/replies"
	);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        log.info("요청 URI: {}", path);

        // 예외 경로는 통과
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            log.info("TokenCheckFilter skip: {}", path);
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

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization 헤더가 없습니다.");
        }

        String token = header.substring(7); // "Bearer " 제거

        Map<String, Object> claims = jwtTokenProvider.validateToken(token);
        String email = (String) claims.get("userEmail");

        log.info("인증된 이메일: {}", email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
