package com.chaewsstore.admin.apis.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto(
    @NotBlank String refreshToken
) {

}
