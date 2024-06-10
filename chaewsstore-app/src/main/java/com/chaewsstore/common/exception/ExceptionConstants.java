package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ResponseCode;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ResponseCode.INVALID_PASSWORD);
    UnauthorizedException INVALID_REFRESH_TOKEN = new UnauthorizedException(ResponseCode.INVALID_REFRESH_TOKEN);

    ForbiddenException FORBIDDEN_BID = new ForbiddenException(ResponseCode.FORBIDDEN_BID);

    NotFoundException NOT_FOUND_ACCOUNT = new NotFoundException(ResponseCode.NOT_FOUND_ACCOUNT);
    NotFoundException NOT_FOUND_REFRESH_TOKEN = new NotFoundException(ResponseCode.NOT_FOUND_REFRESH_TOKEN);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ResponseCode.NOT_FOUND_PRODUCT);
    NotFoundException NOT_FOUND_BID = new NotFoundException(ResponseCode.NOT_FOUND_BID);

    DuplicateException ACCOUNT_DUPLICATION = new DuplicateException(ResponseCode.ACCOUNT_DUPLICATION);
    DuplicateException NICKNAME_DUPLICATION = new DuplicateException(ResponseCode.NICKNAME_DUPLICATION);
    DuplicateException DUPLICATION_BID = new DuplicateException(ResponseCode.DUPLICATION_BID);
}
