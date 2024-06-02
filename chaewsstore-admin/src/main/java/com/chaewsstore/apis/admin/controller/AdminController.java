package com.chaewsstore.apis.admin.controller;

import com.chaewsstore.apis.admin.dto.AdminResponseDto;
import com.chaewsstore.apis.admin.dto.AdminSignupRequestDto;
import com.chaewsstore.apis.admin.usecase.AdminUseCase;
import com.chaewsstore.common.response.ResponseCode;
import com.chaewsstore.common.response.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminUseCase adminUseCase;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<AdminResponseDto> signup(
        @Valid @RequestBody AdminSignupRequestDto request) {
        AdminResponseDto data = adminUseCase.signup(request);
        return ResponseData.of(ResponseCode.SIGNUP_SUCCESS, data);
    }

    @GetMapping("/check-username/{username}/exists")
    public ResponseData checkUsername(@PathVariable String username) {
        adminUseCase.checkUsername(username);
        return ResponseData.from(ResponseCode.CHECK_USERNAME_SUCCESS);
    }
}
