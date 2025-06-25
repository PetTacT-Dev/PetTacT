package com.pettact.api.security.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeStore {
    private final Map<String, VerificationInfo> store = new ConcurrentHashMap<>();

    // 코드 저장 (번호든 토큰이든)
    public void saveCode(String key, String code) {
        store.put(key, new VerificationInfo(code, LocalDateTime.now().plusMinutes(5)));
    }

    // 코드 직접 검증
    public boolean verifyCode(String key, String inputCode) {
        VerificationInfo info = store.get(key);
        if (info == null || info.expiredAt.isBefore(LocalDateTime.now())) {
            store.remove(key);
            return false;
        }
        return info.code.equals(inputCode);
    }

    // 저장된 값 직접 가져오기 (링크 인증 -> 토큰에서 이메일(key)를 꺼내야함)
    public String getCode(String key) {
        VerificationInfo info = store.get(key);
        if (info == null || info.expiredAt.isBefore(LocalDateTime.now())) {
            store.remove(key);
            return null;
        }
        return info.code;
    }

    // 인증 완료되면 키 삭제
    public void remove(String key) {
        store.remove(key);
    }

    // 내부 저장 구조
    private static class VerificationInfo {
        String code;
        LocalDateTime expiredAt;

        VerificationInfo(String code, LocalDateTime expiredAt) {
            this.code = code;
            this.expiredAt = expiredAt;
        }
    }
}
