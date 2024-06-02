package com.chaewsstore.apis.admin.dto;

import static com.chaewsstore.core.common.util.VerificationConstants.PASSWORD_REGEXP;

import com.chaewsstore.core.domain.admin.Admin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminSignupRequestDto(
    @NotNull(message = "이메일은 필수 입력 항목입니다")
    @Email
    String username,

    @NotNull(message = "비밀번호는 필수 입력 항목입니다")
    @Pattern(regexp = PASSWORD_REGEXP, message = "비밀번호는 영문, 숫자, 특수문자 포함 10자 이상 25자 이하여야 합니다.")
    String password,

    @NotNull(message = "이름은 필수 입력 항목입니다")
    String name
) {

    public Admin toEntity(String encodedPassword) {
        return Admin.create(username, encodedPassword, name);
    }
}
