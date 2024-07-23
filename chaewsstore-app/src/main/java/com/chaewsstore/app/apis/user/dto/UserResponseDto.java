package com.chaewsstore.app.apis.user.dto;

import com.chaewsstore.core.domain.user.User;

public record UserResponseDto(
    Long id,
    String username,
    String nickname
) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(),
            user.getNickname());
    }
}
