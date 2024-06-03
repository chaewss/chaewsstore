package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ResponseCode;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ResponseCode.INVALID_PASSWORD);
    DuplicateException ADMIN_DUPLICATION = new DuplicateException(ResponseCode.ADMIN_DUPLICATION);
    NotFoundException NOT_FOUND_ADMIN = new NotFoundException(ResponseCode.NOT_FOUND_ADMIN);

}
