package com.chaewsstore.core.domain.bid;

import static com.globalutils.exception.StatusCode.BAD_REQUEST;
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

    /* 400 BAD_REQUEST */
    BID_NOT_IN_LIVE(BAD_REQUEST, "입찰이 이미 진행중입니다"),
    BID_NOT_IN_TRANSACTION(BAD_REQUEST, "해당 입찰은 거래 중 상태가 아닙니다"),
    BID_NOT_INSPECT(BAD_REQUEST, "상품이 검수되지 않았습니다"),

    /* 403 FORBIDDEN */
    FORBIDDEN_BID(FORBIDDEN, "해당 입찰에 대한 권한이 없습니다"),

    /* 404 NOT_FOUND */
    NOT_FOUND_BID(NOT_FOUND, "존재하지 않는 입찰입니다"),
    NOT_FOUND_BID_WITH_CONDITION(NOT_FOUND, "주어진 조건에 맞는 입찰이 존재하지 않습니다"),
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
