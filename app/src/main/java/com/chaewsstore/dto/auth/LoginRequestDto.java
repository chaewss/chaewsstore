package com.chaewsstore.dto.auth;

public record LoginRequestDto(
    String email,
    String password
) {

}
