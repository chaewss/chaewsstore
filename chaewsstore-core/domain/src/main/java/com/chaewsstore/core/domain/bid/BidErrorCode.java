package com.chaewsstore.core.domain.bid;

import static com.globalutils.exception.StatusCode.CONFLICT;
import static com.globalutils.exception.StatusCode.FORBIDDEN;
import static com.globalutils.exception.StatusCode.NOT_FOUND;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BidErrorCode implements BaseErrorCode {

    /* 403 FORBIDDEN */
    FORBIDDEN_BID(FORBIDDEN, "해당 입찰에 대한 권한이 없습니다"),

    /* 404 NOT_FOUND */
    NOT_FOUND_BID(NOT_FOUND, "존재하지 않는 입찰입니다"),

    /* 409 CONFLICT */
    DUPLICATION_BID(CONFLICT, "중복된 입찰입니다"),
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
