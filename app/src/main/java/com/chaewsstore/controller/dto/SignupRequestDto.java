package com.chaewsstore.controller.dto;

import static com.chaewsstore.util.VerificationUtil.NICKNAME_REGEXP;
import static com.chaewsstore.util.VerificationUtil.PASSWORD_REGEXP;

import com.chaewsstore.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String username;

    @NotNull(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = PASSWORD_REGEXP, message = "비밀번호는 영문, 숫자, 특수문자 포함 10자 이상 25자 이하여야 합니다.")
    private String password;

    @NotNull(message = "닉네임은 필수 입력 항목입니다.")
    @Pattern(regexp = NICKNAME_REGEXP, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    private String nickname;

    @Builder
    public SignupRequestDto(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public Account toAccount(PasswordEncoder passwordEncoder) {
        return Account.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .nickname(nickname)
            .build();
    }

}
