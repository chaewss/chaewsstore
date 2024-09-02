package com.chaewsstore.core.infra.importer;

import com.chaewsstore.core.infra.jwt.TokenProvider;
import com.chaewsstore.core.infra.p6spy.P6SpyConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChaewsstoreConfigGroup {

    JWT(TokenProvider.class),
    P6SPY(P6SpyConfig.class),
    ;

    private final Class<? extends ChaewsstoreConfig> configClass;
}
