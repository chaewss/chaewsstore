package com.chaewsstore.dto.auth;

import com.chaewsstore.auth.TokenDto;

public record LoginResponseDto(
    Long userId,
    TokenDto token
) {

}
