package com.pettact.api.security.util;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${com.pettact.api.jwt.secret}")
    private String secretKey;	// 서버에서만 알고 있는 비밀키
    
	public byte[] getSecretKey() {
		try {
			log.info("key = {}", secretKey);
			return Base64.getEncoder().encode(secretKey.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 토큰 생성
    public String generateToken(Map<String, Object> valueMap, int days){

        log.info("generateKey...  : " + secretKey);

        //헤더 부분
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ","JWT");
        headers.put("alg","HS256");

        //payload 부분 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        // TODO: 테스트용으로 토큰 7일/10일으로 변경
        int time = (1) * days;

        //10분 단위로 조정
        //int time = (10) * days; //테스트는 분단위로 나중에 60*24 (일)단위변경
        
        try {
		        String jwtStr = Jwts.builder()
	                .header().add(valueMap) //헤더 부분
	                .and().claims(payloads) //payload 부분 설정
	                .issuedAt(Date.from(ZonedDateTime.now().toInstant())) //JWT 발급시간 설정 
//	                .expiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant())) //만료기간 설정 
	                .expiration(Date.from(ZonedDateTime.now().plusDays(time).toInstant())) //만료기간 설정
	                .signWith(Keys.hmacShaKeyFor(getSecretKey()), Jwts.SIG.HS256)
	                .compact();
		
		        log.info("jwtStr...  : " + jwtStr);
		        return jwtStr;
        } catch (Exception e) {
	        e.printStackTrace();
        }
        return null;
    }
    
    // 토큰 검증
    public Map<String, Object> validateToken(String token)throws JwtException {

        Map<String, Object> claim = null;

        //인증 토큰 문자열을 이용하여 클래임 객체를 얻는다
        claim = Jwts.parser()
        		.verifyWith(Keys.hmacShaKeyFor(getSecretKey()))
  				.build()
  				.parseSignedClaims(token)
  				.getPayload();
      		
        return claim;
    }

}

