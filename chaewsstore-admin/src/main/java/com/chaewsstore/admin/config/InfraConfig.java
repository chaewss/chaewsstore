package com.chaewsstore.admin.config;

import com.chaewsstore.core.infra.importer.ChaewsstoreConfigGroup;
import com.chaewsstore.core.infra.importer.EnableChaewsstoreConfig;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableChaewsstoreConfig({
    ChaewsstoreConfigGroup.JWT,
})
class InfraConfig {

}
