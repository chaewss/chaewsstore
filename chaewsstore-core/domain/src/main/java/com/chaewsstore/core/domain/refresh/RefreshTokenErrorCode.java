package com.chaewsstore.core.domain.refresh;

import static com.globalutils.exception.StatusCode.NOT_FOUND;
import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefreshTokenErrorCode implements BaseErrorCode {

    /* 401 UNAUTHORIZED */
    WITHOUT_OWNERSHIP_REFRESH_TOKEN(UNAUTHORIZED, "소유권이 없는 리프레시 토큰입니다"),

    /* 404 NOT_FOUND */
    NOT_FOUND_REFRESH_TOKEN(NOT_FOUND, "존재하지 않는 리프레시 토큰입니다"),
    ;

    private final StatusCode statusCode;
    private final String detail;

    @Override
    public String statusCode() {
        return name();
    }

    @Override
    public String getExplainDetail() {
        return detail;
    }
}
