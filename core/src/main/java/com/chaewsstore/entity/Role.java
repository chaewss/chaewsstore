package com.chaewsstore.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ASSOCIATE("ROLE_ASSOCIATE"),
    ADMIN("ROLE_ADMIN");

    private final String key;

}
