package com.globalutils.response;

import com.globalutils.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends Response {

    private final String code;
    private final String detail;

    private ErrorResponse(String code, String detail) {
        this.code = code;
        this.detail = detail;
    }

    public static ErrorResponse from(BaseErrorCode errorCode) {
        return new ErrorResponse(errorCode.statusCode(), errorCode.getExplainDetail());
    }

    public static ErrorResponse of(BaseErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.statusCode(), message);
    }
}
