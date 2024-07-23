package com.chaewsstore.admin.apis.admin.controller;

import com.chaewsstore.admin.apis.admin.dto.AdminResponseDto;
import com.chaewsstore.admin.apis.admin.dto.AdminSignupRequestDto;
import com.chaewsstore.admin.apis.admin.usecase.AdminUseCase;
import com.globalutils.response.SuccessResponse;
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
    public SuccessResponse<AdminResponseDto> signup(
        @Valid @RequestBody AdminSignupRequestDto request) {
        AdminResponseDto data = adminUseCase.signup(request);
        return SuccessResponse.from(data);
    }

    @GetMapping("/check-username/{username}/exists")
    public SuccessResponse checkUsername(@PathVariable String username) {
        adminUseCase.checkUsername(username);
        return SuccessResponse.create();
    }
}
