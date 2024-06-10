package com.chaewsstore.apis.account.controller;

import com.chaewsstore.apis.account.dto.AccountResponseDto;
import com.chaewsstore.apis.account.dto.SignupRequestDto;
import com.chaewsstore.apis.account.usecase.AccountUseCase;
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
@RequestMapping("api/accounts")
public class AccountController {

    private final AccountUseCase accountUseCase;

    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<AccountResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        return SuccessResponse.from(accountUseCase.signup(request));
    }

    @GetMapping("check-username/{username}/exists")
    public SuccessResponse checkUsername(@PathVariable String username) {
        accountUseCase.checkUsername(username);
        return SuccessResponse.create();
    }

    @GetMapping("check-nickname/{nickname}/exists")
    public SuccessResponse checkNickname(@PathVariable String nickname) {
        accountUseCase.checkNickname(nickname);
        return SuccessResponse.create();
    }
}
