package com.chaewsstore.core.domain.admin;

import static com.globalutils.exception.StatusCode.CONFLICT;
import static com.globalutils.exception.StatusCode.NOT_FOUND;
import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements BaseErrorCode {

    /* 401 UNAUTHORIZED */
    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),

    /* 404 NOT_FOUND */
    NOT_FOUND_ADMIN(NOT_FOUND, "존재하지 않는 어드민입니다"),

    /* 409 CONFLICT */
    ADMIN_DUPLICATION(CONFLICT, "중복된 아이디입니다"),
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
