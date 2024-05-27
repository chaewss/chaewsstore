package com.chaewsstore.apis.auth.controller;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.service.AuthService;
import com.chaewsstore.core.common.util.ResponseCode;
import com.chaewsstore.core.common.util.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseData<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseData.of(ResponseCode.LOGIN_SUCCESS, authService.login(request));
    }
}
