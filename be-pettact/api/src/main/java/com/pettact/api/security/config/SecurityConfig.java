package com.pettact.api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.security.filter.LoginFilter;
import com.pettact.api.security.filter.RefreshTokenFilter;
import com.pettact.api.security.filter.TokenCheckFilter;
import com.pettact.api.security.service.CustomOAuth2UserService;
import com.pettact.api.security.service.CustomUserDetailsService;
import com.pettact.api.security.util.JwtTokenProvider;
import com.pettact.api.security.vo.CustomOAuth2User;
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService CustomUserDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("------------web configure-------------------");
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
	
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        log.info("------------configure-------------------");
        //인증관리자 빌더 객체 얻기  
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        
        //인증관리자에 userDetailsService와 비밀번호 암호화 객체를 설정한다
        authenticationManagerBuilder
        	.userDetailsService(CustomUserDetailsService)
        	.passwordEncoder(passwordEncoder());
        
        //인증관리자를 생성한다 
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        
        //http 보안 객체에 인증관리자를 설정한다 
        http.authenticationManager(authenticationManager);
        
        //해당 소스 작성후 : 브라우저에서 /generateToken URL을 실행한다
        final LoginFilter loginFilter = new LoginFilter("/v1/user/login", objectMapper, jwtTokenProvider);
        loginFilter.setAuthenticationManager(authenticationManager);
        
        //UsernamePasswordAuthenticationFilter 필더 객체 실행 전에 동작할 loginFilter를 설정한다 
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

        //UsernamePasswordAuthenticationFilter 필더 객체 실행 전에 동작할 TokenCheckFilter 객체를 생성하여 설정한다
        http.addFilterBefore(new TokenCheckFilter(jwtTokenProvider, CustomUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        
        //TokenCheckFilter 필더 객체 실행 전에 동작할 RefreshTokenFilter 객체를 생성하여 설정한다
        //해당 소스 작성후 : 브라우저에서 /refreshToken URL을 실행한다
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", objectMapper, jwtTokenProvider), TokenCheckFilter.class);

        http.csrf(csrf -> csrf.disable());
        //세션을 사용하지 않음
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(authroize -> 
			authroize
            	.requestMatchers(
                    "/v1/user/me",
                    "/v1/user/me/detail",
                    "/v1/user/update",
                    "/v1/user/withdraw"
                ).authenticated()
	        	// TODO: 이거 수정해야함!!! 여기서 페이지마다 권한을 설정하면 됨 - 아래는 권한 설정하는 예시
				.requestMatchers("/v1/admin/**")
				.hasAnyAuthority("ROLE_ADMIN") //반드시 해당 권한만 허가  
				.anyRequest().permitAll() // 나머지는 비로그인 상태에서도 접근 가능
			);

		// 소셜로그인
        http.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService)
            )
            .successHandler((request, response, authentication) -> {
                CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
                Users user = oAuthUser.getUser();

                // JWT claim 설정
                Map<String, Object> claims = Map.of(
                    "userEmail", user.getUserEmail(),
                    "userNo", user.getUserNo(),
                    "userNickname", user.getUserNickname(),
                    "userRole", user.getRoleCode()
                );

                String accessToken = jwtTokenProvider.generateToken(claims, 7);
                String refreshToken = jwtTokenProvider.generateToken(claims, 10);

    			Map<String, String> keyMap = Map.of("accessToken", jwtTokenProvider.generateToken(claims, 7), //Access Token 유효기간 1일로 생성
						"refreshToken", jwtTokenProvider.generateToken(claims, 10)); //Refresh Token 유효기간 10일로 생성


                new ObjectMapper().writeValue(response.getWriter(), keyMap);
                // TODO: 프론트로 리디렉션 url은 설정하면됨
                // response.sendRedirect("http://localhost:5173/oauth2/success?token=" + jwt);
            })
        );
        
        return http.build();
	}
}