package com.chaewsstore.core.infra.exception;

import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements BaseErrorCode {

    EMPTY_ACCESS_TOKEN(UNAUTHORIZED, "토큰이 비어있습니다"),
    INVALID_SIGNATURE(UNAUTHORIZED, "서명이 조작된 토큰입니다"),
    MALFORMED_TOKEN(UNAUTHORIZED, "비정상적인 토큰입니다"),
    EXPIRED_TOKEN(UNAUTHORIZED, "사용기간이 만료된 토큰입니다"),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "지원하지 않는 토큰입니다"),
    ILLEGAL_TOKEN(UNAUTHORIZED, "JWT 토큰이 잘못되었습니다"),
    ;

    private final StatusCode statusCode;
    private final String message;

    @Override
    public String statusCode() {
        return name();
    }

    @Override
    public String getExplainDetail() {
        return message;
    }
}
