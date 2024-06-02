package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotFoundException extends RuntimeException {

    private final ResponseCode responseCode;

}
