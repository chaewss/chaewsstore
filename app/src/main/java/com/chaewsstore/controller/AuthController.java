package com.chaewsstore.controller;

import com.chaewsstore.dto.auth.LoginRequestDto;
import com.chaewsstore.dto.auth.LoginResponseDto;
import com.chaewsstore.service.AuthService;
import com.chaewsstore.util.ResponseCode;
import com.chaewsstore.util.ResponseData;
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
