package com.chaewsstore.apis.account.dto;

import static com.chaewsstore.core.common.util.VerificationUtil.NICKNAME_REGEXP;
import static com.chaewsstore.core.common.util.VerificationUtil.PASSWORD_REGEXP;

import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public record SignupRequestDto(
    @NotNull(message = "이메일은 필수 입력 항목입니다")
    @Email
    String username,

    @NotNull(message = "비밀번호는 필수 입력 항목입니다")
    @Pattern(regexp = PASSWORD_REGEXP, message = "비밀번호는 영문, 숫자, 특수문자 포함 10자 이상 25자 이하여야 합니다.")
    String password,

    @NotNull(message = "닉네임은 필수 입력 항목입니다")
    @Pattern(regexp = NICKNAME_REGEXP, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    String nickname
) {

    public Account toEntity(String encodedPassword, Role role) {
        return Account.create(username, encodedPassword, nickname, role);
    }
}
