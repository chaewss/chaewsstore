package com.chaewsstore.apis.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto(
    @NotBlank String refreshToken
) {

}
