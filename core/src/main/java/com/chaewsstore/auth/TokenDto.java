package com.chaewsstore.auth;

public record TokenDto(
    String accessToken,
    String refreshToken,
    String grantType
) {

    public static TokenDto of(String accessToken, String refreshToken, String tokenType) {
        return new TokenDto(accessToken, refreshToken, tokenType);
    }
}
