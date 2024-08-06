package com.chaewsstore.core.domain;

import com.chaewsstore.core.domain.user.Role;
import com.chaewsstore.core.domain.user.User;

public enum UserFixture {

    USER(1L, "user@gmail.com", "password1!", "userNickname", 1000L, Role.ASSOCIATE, false),
    ANOTHER_USER(2L, "anotherUser@gmail.com", "password1!", "anotherUserNickname", 2000L, Role.ASSOCIATE, false),
    SELLER(3L, "seller@gmail.com", "password1!", "sellerNickname", 1000L, Role.ASSOCIATE, false),
    BUYER(4L, "buyer@gmail.com", "password1!", "buyerNickname", 2000L, Role.ASSOCIATE, false),
    ;

    private final Long id;
    private final String username;
    private final String password;
    private final String nickname;
    private final Long account;
    private final Role role;
    private final Boolean isDeleted;


    UserFixture(Long id, String username, String password, String nickname, Long account, Role role,
        Boolean isDeleted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.account = account;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    public User getUser() {
        return User.builder()
            .id(id)
            .username(username)
            .password(password)
            .nickname(nickname)
            .account(account)
            .role(role)
            .isDeleted(isDeleted)
            .build();
    }
}
