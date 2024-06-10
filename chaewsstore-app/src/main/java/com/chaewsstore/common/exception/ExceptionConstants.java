package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ErrorCode;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
    UnauthorizedException WITHOUT_OWNERSHIP_REFRESH_TOKEN = new UnauthorizedException(ErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN);

    ForbiddenException FORBIDDEN_BID = new ForbiddenException(ErrorCode.FORBIDDEN_BID);

    NotFoundException NOT_FOUND_ACCOUNT = new NotFoundException(ErrorCode.NOT_FOUND_ACCOUNT);
    NotFoundException NOT_FOUND_REFRESH_TOKEN = new NotFoundException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ErrorCode.NOT_FOUND_PRODUCT);
    NotFoundException NOT_FOUND_BID = new NotFoundException(ErrorCode.NOT_FOUND_BID);

    DuplicateException ACCOUNT_DUPLICATION = new DuplicateException(ErrorCode.ACCOUNT_DUPLICATION);
    DuplicateException NICKNAME_DUPLICATION = new DuplicateException(ErrorCode.NICKNAME_DUPLICATION);
    DuplicateException DUPLICATION_BID = new DuplicateException(ErrorCode.DUPLICATION_BID);
}
