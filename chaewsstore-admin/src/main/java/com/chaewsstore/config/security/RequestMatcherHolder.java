package com.chaewsstore.config.security;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.chaewsstore.core.domain.account.Role;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RequestMatcherHolder {

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
        // auth
        new RequestInfo(POST, "/admin/auth/login", null),

        // admin
        new RequestInfo(POST, "/admin/signup", null),
        new RequestInfo(GET, "/admin/check-username/**/exists", null),

        // static resources
        new RequestInfo(GET, "/docs/**", null),
        new RequestInfo(GET, "/*.ico", null),
        new RequestInfo(GET, "/resources/**", null),
        new RequestInfo(GET, "/error", null)
    );
    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    /**
     * 최소 권한이 주어진 요청에 대한 RequestMatcher 반환
     * @param minRole 최소 권한 (Nullable)
     * @return 생성된 RequestMatcher
     */
    public RequestMatcher getRequestMatchersByMinRole(@Nullable Role minRole) {
        var key = getKeyByRole(minRole);
        return reqMatcherCacheMap.computeIfAbsent(key, k ->
            new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                .filter(reqInfo -> Objects.equals(reqInfo.minRole(), minRole))
                .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(),
                    reqInfo.method().name()))
                .toArray(AntPathRequestMatcher[]::new)));
    }

    private String getKeyByRole(@Nullable Role minRole) {
        return minRole == null ? "VISITOR" : minRole.name();
    }

    private record RequestInfo(HttpMethod method, String pattern, Role minRole) {

    }
}
