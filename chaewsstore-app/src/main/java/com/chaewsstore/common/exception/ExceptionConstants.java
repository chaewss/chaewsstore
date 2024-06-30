package com.chaewsstore.common.exception;

import com.chaewsstore.core.domain.bid.BidErrorCode;
import com.chaewsstore.core.domain.product.ProductErrorCode;
import com.chaewsstore.core.domain.refresh.RefreshTokenErrorCode;
import com.chaewsstore.core.domain.user.UserErrorCode;
import com.globalutils.exception.BadRequestException;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;

public interface ExceptionConstants {

    BadRequestException INSUFFICIENT_BALANCE = new BadRequestException(UserErrorCode.INSUFFICIENT_BALANCE);
    BadRequestException BID_NOT_IN_LIVE = new BadRequestException(BidErrorCode.BID_NOT_IN_LIVE);

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(UserErrorCode.INVALID_PASSWORD);
    UnauthorizedException WITHOUT_OWNERSHIP_REFRESH_TOKEN = new UnauthorizedException(RefreshTokenErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN);

    ForbiddenException FORBIDDEN_BID = new ForbiddenException(BidErrorCode.FORBIDDEN_BID);

    NotFoundException NOT_FOUND_USER = new NotFoundException(UserErrorCode.NOT_FOUND_USER);
    NotFoundException NOT_FOUND_REFRESH_TOKEN = new NotFoundException(RefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ProductErrorCode.NOT_FOUND_PRODUCT);
    NotFoundException NOT_FOUND_BID = new NotFoundException(BidErrorCode.NOT_FOUND_BID);
    NotFoundException NOT_FOUND_BID_WITH_CONDITION = new NotFoundException(BidErrorCode.NOT_FOUND_BID_WITH_CONDITION);

    DuplicateException USER_DUPLICATION = new DuplicateException(UserErrorCode.USER_DUPLICATION);
    DuplicateException NICKNAME_DUPLICATION = new DuplicateException(UserErrorCode.NICKNAME_DUPLICATION);
}
