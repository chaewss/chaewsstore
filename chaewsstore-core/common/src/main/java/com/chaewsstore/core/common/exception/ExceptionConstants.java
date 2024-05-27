package com.chaewsstore.core.common.exception;

import com.chaewsstore.core.common.util.ResponseCode;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ResponseCode.INVALID_PASSWORD);

    ForbiddenException FORBIDDEN_BID = new ForbiddenException(ResponseCode.FORBIDDEN_BID);

    NotFoundException NOT_FOUND_ACCOUNT = new NotFoundException(ResponseCode.NOT_FOUND_ACCOUNT);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ResponseCode.NOT_FOUND_PRODUCT);
    NotFoundException NOT_FOUND_BID = new NotFoundException(ResponseCode.NOT_FOUND_BID);

    DuplicateException ACCOUNT_DUPLICATION = new DuplicateException(ResponseCode.ACCOUNT_DUPLICATION);
    DuplicateException NICKNAME_DUPLICATION = new DuplicateException(ResponseCode.NICKNAME_DUPLICATION);
    DuplicateException DUPLICATION_BID = new DuplicateException(ResponseCode.DUPLICATION_BID);

}
