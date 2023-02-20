package com.chaewsstore.controller;

import com.chaewsstore.controller.dto.AccountResponseDto;
import com.chaewsstore.controller.dto.SignupRequestDto;
import com.chaewsstore.service.AccountService;
import com.chaewsstore.util.ResponseCode;
import com.chaewsstore.util.ResponseData;
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

    private final AccountService accountService;

    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<AccountResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        AccountResponseDto data = accountService.signup(request);
        return ResponseData.of(ResponseCode.SIGNUP_SUCCESS, data);
    }

    @GetMapping("check-username/{username}/exists")
    public ResponseData checkUsername(@PathVariable String username) {
        accountService.checkUsername(username);
        return ResponseData.of(ResponseCode.CHECK_USERNAME_SUCCESS);
    }

    @GetMapping("check-nickname/{nickname}/exists")
    public ResponseData checkNickname(@PathVariable String nickname) {
        accountService.checkNickname(nickname);
        return ResponseData.of(ResponseCode.CHECK_NICKNAME_SUCCESS);
    }

}
