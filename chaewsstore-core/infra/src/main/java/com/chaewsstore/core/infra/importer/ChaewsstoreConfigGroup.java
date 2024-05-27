package com.chaewsstore.core.infra.importer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChaewsstoreConfigGroup {

    ;

    private final Class<? extends ChaewsstoreConfig> configClass;
}
