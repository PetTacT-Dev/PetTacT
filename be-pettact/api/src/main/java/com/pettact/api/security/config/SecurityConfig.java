package com.pettact.api.security.config;

import java.util.Map;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        log.info("------------web configure-------------------");
//        return (web) -> web.ignoring()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("------------web configure-------------------");
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/ws-stomp/**")
        		.requestMatchers("/files/**");
    }
    
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        log.info("------------configure-------------------");

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        final LoginFilter loginFilter = new LoginFilter("/v1/user/login", objectMapper, jwtTokenProvider);
        loginFilter.setAuthenticationManager(authenticationManager);
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new TokenCheckFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", objectMapper, jwtTokenProvider), TokenCheckFilter.class);

        http.csrf(csrf -> csrf.disable())
        		.cors(cors -> {});

        http.authorizeHttpRequests(authorize ->
                authorize
                .requestMatchers("/v1/multifile/image/**").permitAll()
            	.requestMatchers("/v1/notification/subscribe").permitAll()
            	.requestMatchers("/v1/notification/**").authenticated()
            	.requestMatchers("/v1/user/mypage/**").authenticated()
            	.requestMatchers("/v1/user/seller/**").authenticated()
            	.requestMatchers("/v1/user/social/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/v1/product/create").hasAuthority("ROLE_SELLER")
                .requestMatchers(HttpMethod.PUT, "/v1/product/update/*").hasAuthority("ROLE_SELLER")
                .requestMatchers(HttpMethod.POST, "/v1/product/delete/*").hasAuthority("ROLE_SELLER")
                .requestMatchers("/v1/board-categories").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/v1/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/v1/payments/**").permitAll()	// TODO: 결제 인증 해결
                .requestMatchers("/files/**").permitAll()	// TODO: 결제 인증 해결
                .anyRequest().permitAll()
        );

        http.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler((request, response, authentication) -> {
                CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
                Users user = oAuthUser.getUser();

                Map<String, Object> claims = Map.of(
                        "userEmail", user.getUserEmail(),
                        "userNo", user.getUserNo(),
                        "userNickname", user.getUserNickname(),
                        "userRole", user.getRoleCode(),
                        "userStatus", user.getStatusCode()
                );

                String accessToken = jwtTokenProvider.generateToken(claims, 7);
                String refreshToken = jwtTokenProvider.generateToken(claims, 10);

                String redirectUrl = "http://localhost:5173/oauth2/success"
                        + "?accessToken=" + accessToken
                        + "&refreshToken=" + refreshToken;

                response.sendRedirect(redirectUrl);
            })
            .failureHandler((request, response, exception) -> {
                String redirectUrl = "http://localhost:5173/user/login?error=EMAIL_ALREADY_EXISTS";
                response.sendRedirect(redirectUrl);
            })
        );

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
