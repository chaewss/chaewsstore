package com.chaewsstore.admin.apis.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequestDto(
    @NotBlank
    String accessToken,

    @NotBlank
    String refreshToken
) {

}
