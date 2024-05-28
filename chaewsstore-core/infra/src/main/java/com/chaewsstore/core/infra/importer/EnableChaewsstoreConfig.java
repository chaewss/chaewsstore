package com.chaewsstore.core.infra.importer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ChaewsstoreConfigImportSelector.class)
public @interface EnableChaewsstoreConfig {

    ChaewsstoreConfigGroup[] value();
}
