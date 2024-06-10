package com.chaewsstore.core.domain.brand;

import static com.globalutils.exception.StatusCode.CONFLICT;
import static com.globalutils.exception.StatusCode.NOT_FOUND;
import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BrandErrorCode  implements BaseErrorCode {

    /* 404 NOT_FOUND */
    NOT_FOUND_BRAND(NOT_FOUND, "존재하지 않는 브랜드입니다"),
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
