package com.chaewsstore.app.apis.auth.dto;

import com.chaewsstore.core.infra.jwt.Jwts;

public record LoginResponseDto(
    Long userId,
    Jwts token
) {

}
