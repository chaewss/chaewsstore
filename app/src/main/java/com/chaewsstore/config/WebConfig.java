package com.chaewsstore.config;

import com.chaewsstore.util.LoginAccountArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Component
public class WebConfig implements WebMvcConfigurer {

    private final LoginAccountArgumentResolver loginAccountArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAccountArgumentResolver);
    }

}
