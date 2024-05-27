package com.chaewsstore.apis.account.dto;

import com.chaewsstore.core.domain.account.Account;

public record AccountResponseDto(
    Long id,
    String username,
    String nickname
) {

    public static AccountResponseDto from(Account account) {
        return new AccountResponseDto(account.getId(), account.getUsername(),
            account.getNickname());
    }
}
