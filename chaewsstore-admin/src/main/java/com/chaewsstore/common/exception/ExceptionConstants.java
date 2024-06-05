package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ResponseCode;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ResponseCode.INVALID_PASSWORD);
    UnauthorizedException INVALID_REFRESH_TOKEN = new UnauthorizedException(ResponseCode.INVALID_REFRESH_TOKEN);

    NotFoundException NOT_FOUND_ADMIN = new NotFoundException(ResponseCode.NOT_FOUND_ADMIN);
    NotFoundException NOT_FOUND_REFRESH_TOKEN = new NotFoundException(ResponseCode.NOT_FOUND_REFRESH_TOKEN);
    NotFoundException NOT_FOUND_BRAND = new NotFoundException(ResponseCode.NOT_FOUND_BRAND);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ResponseCode.NOT_FOUND_PRODUCT);

    DuplicateException ADMIN_DUPLICATION = new DuplicateException(ResponseCode.ADMIN_DUPLICATION);
    DuplicateException DUPLICATE_PRODUCT = new DuplicateException(ResponseCode.DUPLICATE_PRODUCT);
}
