package com.chaewsstore.core.domain;

import com.chaewsstore.core.domain.admin.Admin;

public enum AdminFixture {

    ADMIN(1L, "admin@gmail.com", "password1!", "admin1", false),
    ANOTHER_ADMIN(2L, "anotherAdmin@gmail.com", "password1!", "admin2", false),
    ;

    private final Long id;
    private final String username;
    private final String password;
    private final String name;
    private final Boolean isDeleted;


    AdminFixture(Long id, String username, String password, String name, Boolean isDeleted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.isDeleted = isDeleted;
    }

    public Admin getAdmin() {
        return Admin.builder()
            .id(id)
            .username(username)
            .password(password)
            .name(name)
            .isDeleted(isDeleted)
            .build();
    }
}
