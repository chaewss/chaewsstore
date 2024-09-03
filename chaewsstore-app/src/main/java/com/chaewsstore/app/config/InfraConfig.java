package com.chaewsstore.app.config;

import com.chaewsstore.core.infra.importer.ChaewsstoreConfigGroup;
import com.chaewsstore.core.infra.importer.EnableChaewsstoreConfig;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableChaewsstoreConfig({
    ChaewsstoreConfigGroup.JWT,
    ChaewsstoreConfigGroup.P6SPY,
})
class InfraConfig {

}
