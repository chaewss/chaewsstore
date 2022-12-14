package com.chaewsscode.chaewsstore.auth.controller;

import com.chaewsscode.chaewsstore.auth.controller.dto.AccountInfoResponseDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.AccountResponseDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.ResetPasswordRequestDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.SigninRequestDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.SignupRequestDto;
import com.chaewsscode.chaewsstore.auth.service.AuthService;
import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.util.LoginAccount;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.ResponseData;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("signin")
    public ResponseEntity<ResponseData<AccountResponseDto>> signin(
        @RequestBody @Valid SigninRequestDto request) {
        AccountResponseDto data = authService.signin(request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.SIGNIN_SUCCESS, data);
    }

    // 로그아웃
    @GetMapping("signout")
    public ResponseEntity<ResponseData> signout(
        @RequestParam("refresh-token") @NotBlank String refreshToken) {
        authService.signout(refreshToken);
        return ResponseData.toResponseEntity(ResponseCode.SIGNOUT_SUCCESS);
    }

    // 회원가입
    @PostMapping("signup")
    public ResponseEntity<ResponseData> signup(@Valid @RequestBody SignupRequestDto request) {
        authService.signup(request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.SIGNUP_SUCCESS);
    }

    // 회원 상세 정보 조회
    @GetMapping("my-page")
    public ResponseEntity<ResponseData<AccountInfoResponseDto>> readAccountInfo(@LoginAccount Account account) {
        AccountInfoResponseDto data = authService.readAccountInfo(account);
        return ResponseData.toResponseEntity(ResponseCode.READ_ACCOUNT_INFO_SUCCESS, data);
    }

    // 비밀번호 재설정
    @PostMapping("reset-password")
    public ResponseEntity<ResponseData> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request);
        return ResponseData.toResponseEntity(ResponseCode.RESET_PASSWORD_SUCCESS);
    }
}
