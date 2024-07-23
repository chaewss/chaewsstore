package com.chaewsstore.admin.apis.auth.dto;

import com.chaewsstore.core.infra.jwt.Jwts;

public record LoginResponseDto(
    Long userId,
    Jwts token
) {

}
