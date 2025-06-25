package com.pettact.api.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.security.util.JwtTokenProvider;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.entity.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	private static final String CONTENT_TYPE = "application/json";//json 타입의 데이터로만 로그인을 진행한다.
	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;
	
	public LoginFilter(String defaultFilterPrrocessingUrl, ObjectMapper objectMapper, JwtTokenProvider jwtTokenProvider) {
		super(defaultFilterPrrocessingUrl);
		this.objectMapper = objectMapper;
		this.jwtTokenProvider = jwtTokenProvider;

		//로그인 성공시 처리 핸들러 등록  
		this.setAuthenticationSuccessHandler((request, response, authentication) -> {
			//authentication : 인증된 로그인 정보 
			log.info("로그인 성공시 처리 핸들러 ............." );
			log.info("인증된 로그인 정보 : {}", authentication);
			log.info("인증된 로그인 아이디 : {}", authentication.getName());
			
		    // Users 객체를 principal에서 꺼냄
			CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
			Users user = customUserDetails.getUserEntity();
			
			//JWT에 추가할 정보로 아이디가 있는 Map 객체를 생성한다
		    Map<String, Object> claims = Map.of(
		            "userEmail", user.getUserEmail(),
		            "userNo", user.getUserNo(),
		            "userNickname", user.getUserNickname(),
		            "userRole", user.getRoleCode()
		        );

			Map<String, Object> keyMap = Map.of(
				    "accessToken", jwtTokenProvider.generateToken(claims, 7),	//Access Token 유효기간 1일
				    "refreshToken", jwtTokenProvider.generateToken(claims, 10),	//Refresh Token 유효기간 10일
				    "user", Map.of(
				    		"userEmail", user.getUserEmail(),
				        "userNo", user.getUserNo(),
				        "userNickname", user.getUserNickname(),
				        "userRole", user.getRoleCode()
				    )
				);
			//json 객체로 응답 스트림에 keyMap 객체를 출력 한다 
			objectMapper.writeValue(response.getWriter(), keyMap);
		});
		//로그인 실패시 처리 핸들러 등록  
		this.setAuthenticationFailureHandler((request, response, exception) -> {
			log.info("로그인 실패시 처리 핸들러 등록 ............." );
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		log.info("LoginFilter ----------------------------- ");
		
		log.info("ContentType = {}", request.getContentType());
		if(request.getContentType() == null || !request.getContentType().startsWith(CONTENT_TYPE)  ) {
			log.info("Authentication Content-Type not supported: " + request.getContentType());
			return null;
		}

		//json으로 요청한 문자열을 얻는다
		String jsonText = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

		//JSON 문자열을 Map 객체로 변환 한다
		Map<String, String> jsonData = objectMapper.readValue(jsonText, Map.class);
		log.info("jsonData = {}", jsonData);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jsonData.get("userEmail"), jsonData.get("userPassword"));
		return getAuthenticationManager().authenticate(authenticationToken);
	}
	
	
}
