package com.pettact.api.security.service;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.repository.CommonCodeRepository;
import com.pettact.api.security.vo.CustomOAuth2User;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final CommonCodeRepository commonCodeRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = extractEmail(provider, attributes);
        String providerId = extractProviderId(provider, attributes);

        if (email == null || providerId == null) {
            throw new OAuth2AuthenticationException("소셜 로그인 정보 추출 실패");
        }

        // 권한
        CommonCode role = commonCodeRepository.findById("ROLE_NORMAL")
            .orElseThrow(() -> new IllegalStateException("기본 권한 코드 없음"));
        
        // 상태
        CommonCode status = commonCodeRepository.findById("STATUS_ACTIVE")
            .orElseThrow(() -> new IllegalStateException("기본 상태 코드 없음"));
        
        Optional<Users> existing = userRepository.findByUserEmail(email);
        
        if (existing.isPresent()) {
            Users existingUser = existing.get();
            
            if (!provider.equals(existingUser.getProvider())) {
            	log.error("이미 이메일입니다. provider [{}], email [{}]", provider, email);
                throw new OAuth2AuthenticationException("이미 다른 플랫폼으로 가입된 이메일입니다.");
            }

            return new CustomOAuth2User(existingUser, attributes);
        }
        
        Users newUser = Users.builder()
                .userEmail(email)
                .provider(provider)
                .providerId(providerId)
                .userName(extractName(provider, attributes))
                .userNickname(generateUniqueNickname(email))
                .userEmailChecked(true)
                .userHasPet(false)
                .userBlacklist(false)
                .roleCode(role)
                .statusCode(status)
                .build();

        Users saved = userRepository.save(newUser);
        return new CustomOAuth2User(saved, attributes);
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("email");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        return null;
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub"); // Google은 sub가 고유 ID
        } else if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id")); // long → string 변환
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("id");
        }
        return null;
    }
    
    private String extractName(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> props = (Map<String, Object>) attributes.get("properties");
            return (String) props.get("nickname");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        }
        return "Unknown";
    }

    private String generateUniqueNickname(String email) {
        String base = email.split("@")[0];
        String nickname = base;
        int i = 1;
        while (userRepository.existsByUserNickname(nickname)) {
            nickname = base + i++;
        }
        return nickname;
    }
}

