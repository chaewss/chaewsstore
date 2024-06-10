package com.chaewsstore.core.infra.jwt;

public record Jwts(
    String accessToken,
    String refreshToken,
    String grantType
) {

    public static Jwts of(String accessToken, String refreshToken, String tokenType) {
        return new Jwts(accessToken, refreshToken, tokenType);
    }
}
