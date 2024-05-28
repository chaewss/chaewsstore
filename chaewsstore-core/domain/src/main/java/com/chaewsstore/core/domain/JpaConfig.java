package com.chaewsstore.core.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EntityScan("com.chaewsstore.core.domain")
@EnableJpaRepositories("com.chaewsstore.core.domain")
@Configuration
public class JpaConfig {

}
