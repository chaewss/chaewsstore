package com.chaewsstore.app.apis.user.controller;

import com.chaewsstore.app.apis.user.dto.SignupRequestDto;
import com.chaewsstore.app.apis.user.dto.UserResponseDto;
import com.chaewsstore.app.apis.user.usecase.UserUseCase;
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
@RequestMapping("api/users")
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        return SuccessResponse.from(userUseCase.signup(request));
    }

    @GetMapping("check-username/{username}/exists")
    public SuccessResponse checkUsername(@PathVariable String username) {
        userUseCase.checkUsername(username);
        return SuccessResponse.create();
    }

    @GetMapping("check-nickname/{nickname}/exists")
    public SuccessResponse checkNickname(@PathVariable String nickname) {
        userUseCase.checkNickname(nickname);
        return SuccessResponse.create();
    }
}
