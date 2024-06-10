package com.chaewsstore.core.domain.product;

import static com.globalutils.exception.StatusCode.CONFLICT;
import static com.globalutils.exception.StatusCode.NOT_FOUND;
import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

    /* 404 NOT_FOUND */
    NOT_FOUND_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다"),

    /* 409 CONFLICT */
    DUPLICATE_PRODUCT(CONFLICT, "중복된 상품입니다"),
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
