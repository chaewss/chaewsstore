package com.chaewsstore.apis.auth.dto;

import com.chaewsstore.common.security.jwt.Jwts;

public record ReissueTokenResponseDto(
    Long userId,
    Jwts token
) {

}
