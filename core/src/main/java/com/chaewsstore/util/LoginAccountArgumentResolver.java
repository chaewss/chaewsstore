package com.chaewsstore.util;

import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_ACCOUNT;

import com.chaewsstore.auth.TokenProvider;
import com.chaewsstore.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginAccountArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAccountAnnotation = parameter.hasParameterAnnotation(LoginAccount.class);
        boolean isAccountType = Account.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAccountAnnotation && isAccountType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = tokenProvider.extractToken(request);
        return tokenProvider.getLoginAccount(accessToken).orElseThrow(() -> NOT_FOUND_ACCOUNT);
    }
}
