package com.chaewsstore.controller.dto;

import com.chaewsstore.entity.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AccountResponseDto {

    private final Long id;
    private final String username;
    private final String nickname;

    @Builder
    public AccountResponseDto(Long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public static AccountResponseDto of(Account account) {
        return AccountResponseDto.builder()
            .id(account.getId())
            .username(account.getUsername())
            .nickname(account.getNickname())
            .build();
    }

}
