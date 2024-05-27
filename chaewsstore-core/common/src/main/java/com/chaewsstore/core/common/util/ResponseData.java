package com.chaewsstore.core.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    public static ResponseData from(ResponseCode responseCode) {
        return ResponseData.builder()
            .code(responseCode.name())
            .detail(responseCode.getDetail())
            .build();
    }

    public static <T> ResponseData<T> of(ResponseCode responseCode, T data) {
        return ResponseData.<T>builder()
            .code(responseCode.name())
            .detail(responseCode.getDetail())
            .data(data)
            .build();
    }
}
