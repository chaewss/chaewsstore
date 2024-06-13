package com.chaewsstore.apis.user.dto;

import static com.chaewsstore.common.util.VerificationConstants.NICKNAME_MESSAGE;
import static com.chaewsstore.common.util.VerificationConstants.NICKNAME_REGEXP;
import static com.chaewsstore.common.util.VerificationConstants.PASSWORD_MESSAGE;
import static com.chaewsstore.common.util.VerificationConstants.PASSWORD_REGEXP;

import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignupRequestDto(
    @NotNull(message = "이메일은 필수 입력 항목입니다")
    @Email
    String username,

    @NotNull(message = "비밀번호는 필수 입력 항목입니다")
    @Pattern(regexp = PASSWORD_REGEXP, message = PASSWORD_MESSAGE)
    String password,

    @NotNull(message = "닉네임은 필수 입력 항목입니다")
    @Pattern(regexp = NICKNAME_REGEXP, message = NICKNAME_MESSAGE)
    String nickname
) {

    public User toEntity(String encodedPassword, Role role) {
        return User.create(username, encodedPassword, nickname, role);
    }
}
