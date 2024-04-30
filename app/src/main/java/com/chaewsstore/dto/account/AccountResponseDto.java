package com.chaewsstore.dto.account;

import com.chaewsstore.entity.Account;

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
