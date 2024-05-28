package com.chaewsstore.core.common.exception;

import com.chaewsstore.core.common.util.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotFoundException extends RuntimeException {

    private final ResponseCode responseCode;

}
