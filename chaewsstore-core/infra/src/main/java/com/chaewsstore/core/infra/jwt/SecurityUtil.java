package com.chaewsstore.core.infra.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * 현재 인증된 사용자 username 반환
     *
     * @return 현재 인증된 사용자 username
     * @throws IllegalArgumentException Security Context에 인증 정보가 없거나 username이 없는 경우
     */
    public static String getCurrentUserName() {
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Security Context에 인증 정보가 없습니다");
        }

        return authentication.getName();
    }
}
