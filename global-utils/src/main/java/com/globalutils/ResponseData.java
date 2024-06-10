package com.globalutils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globalutils.exception.BaseResponseCode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ResponseData<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String code;
    private final String detail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static ResponseData from(BaseResponseCode responseCode) {
        return ResponseData.builder()
            .code(responseCode.statusCode())
            .detail(responseCode.getExplainDetail())
            .build();
    }

    public static <T> ResponseData<T> of(BaseResponseCode responseCode, T data) {
        return ResponseData.<T>builder()
            .code(responseCode.statusCode())
            .detail(responseCode.getExplainDetail())
            .data(data)
            .build();
    }
}
