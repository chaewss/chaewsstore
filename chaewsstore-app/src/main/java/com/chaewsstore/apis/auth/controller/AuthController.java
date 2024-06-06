package com.chaewsstore.apis.auth.controller;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.dto.LogoutRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenResponseDto;
import com.chaewsstore.apis.auth.usecase.AuthUseCase;
import com.chaewsstore.common.annotation.LoginAccount;
import com.chaewsstore.common.response.ResponseCode;
import com.chaewsstore.common.response.ResponseData;
import com.chaewsstore.core.domain.account.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseData<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseData.of(ResponseCode.LOGIN_SUCCESS, authUseCase.login(request));
    }

    @PostMapping("/reissue")
    public ResponseData<ReissueTokenResponseDto> reissueToken(
        @RequestBody @Valid ReissueTokenRequestDto request) {
        ReissueTokenResponseDto data = authUseCase.reissueToken(request);
        return ResponseData.of(ResponseCode.REISSUE_TOKEN_SUCCESS, data);
    }

    @PostMapping("/logout")
    public ResponseData<Void> logout(@LoginAccount Account account,
        @Valid @RequestBody LogoutRequestDto request) {
        authUseCase.logout(account, request);
        return ResponseData.from(ResponseCode.LOGOUT_SUCCESS);
    }
}
