package com.globalutils.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GlobalErrorException extends RuntimeException {

    private final BaseErrorCode baseErrorCode;
}
