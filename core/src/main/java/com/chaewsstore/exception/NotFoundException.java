package com.chaewsstore.exception;

import com.chaewsstore.util.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotFoundException extends RuntimeException {

    private final ResponseCode responseCode;

}
