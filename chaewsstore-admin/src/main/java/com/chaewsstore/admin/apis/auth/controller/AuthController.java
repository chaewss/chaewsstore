package com.chaewsstore.admin.apis.auth.controller;

import com.chaewsstore.admin.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.admin.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.admin.apis.auth.dto.LogoutRequestDto;
import com.chaewsstore.admin.apis.auth.dto.ReissueTokenRequestDto;
import com.chaewsstore.admin.apis.auth.dto.ReissueTokenResponseDto;
import com.chaewsstore.admin.common.annotation.LoginAdmin;
import com.chaewsstore.admin.apis.auth.usecase.AuthUseCase;
import com.chaewsstore.core.domain.admin.Admin;
import com.globalutils.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public SuccessResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return SuccessResponse.from(authUseCase.login(request));
    }

    @PostMapping("/reissue")
    public SuccessResponse<ReissueTokenResponseDto> reissueToken(
        @RequestBody @Valid ReissueTokenRequestDto request) {
        return SuccessResponse.from(authUseCase.reissueToken(request));
    }

    @PostMapping("/logout")
    public SuccessResponse<Void> logout(@LoginAdmin Admin admin,
        @Valid @RequestBody LogoutRequestDto request) {
        authUseCase.logout(admin, request);
        return SuccessResponse.create();
    }
}
