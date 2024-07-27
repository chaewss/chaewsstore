package com.chaewsstore.core.infra.importer;

import com.chaewsstore.core.infra.jwt.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChaewsstoreConfigGroup {

    JWT(TokenProvider.class),
    ;

    private final Class<? extends ChaewsstoreConfig> configClass;
}
