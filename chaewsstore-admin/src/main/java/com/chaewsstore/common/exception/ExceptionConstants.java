package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ErrorCode;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
    UnauthorizedException WITHOUT_OWNERSHIP_REFRESH_TOKEN = new UnauthorizedException(ErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN);

    NotFoundException NOT_FOUND_ADMIN = new NotFoundException(ErrorCode.NOT_FOUND_ADMIN);
    NotFoundException NOT_FOUND_REFRESH_TOKEN = new NotFoundException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
    NotFoundException NOT_FOUND_BRAND = new NotFoundException(ErrorCode.NOT_FOUND_BRAND);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ErrorCode.NOT_FOUND_PRODUCT);

    DuplicateException ADMIN_DUPLICATION = new DuplicateException(ErrorCode.ADMIN_DUPLICATION);
    DuplicateException DUPLICATE_PRODUCT = new DuplicateException(ErrorCode.DUPLICATE_PRODUCT);
}
