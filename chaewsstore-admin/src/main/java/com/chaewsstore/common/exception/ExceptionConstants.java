package com.chaewsstore.common.exception;

import com.chaewsstore.core.domain.admin.AdminErrorCode;
import com.chaewsstore.core.domain.adminRefresh.AdminRefreshTokenErrorCode;
import com.chaewsstore.core.domain.bid.BidErrorCode;
import com.chaewsstore.core.domain.brand.BrandErrorCode;
import com.chaewsstore.core.domain.product.ProductErrorCode;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;

public interface ExceptionConstants {

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(AdminErrorCode.INVALID_PASSWORD);
    UnauthorizedException WITHOUT_OWNERSHIP_REFRESH_TOKEN = new UnauthorizedException(AdminRefreshTokenErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN);

    NotFoundException NOT_FOUND_ADMIN = new NotFoundException(AdminErrorCode.NOT_FOUND_ADMIN);
    NotFoundException NOT_FOUND_REFRESH_TOKEN = new NotFoundException(AdminRefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN);
    NotFoundException NOT_FOUND_BRAND = new NotFoundException(BrandErrorCode.NOT_FOUND_BRAND);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ProductErrorCode.NOT_FOUND_PRODUCT);
    NotFoundException NOT_FOUND_BID = new NotFoundException(BidErrorCode.NOT_FOUND_BID);

    DuplicateException ADMIN_DUPLICATION = new DuplicateException(AdminErrorCode.ADMIN_DUPLICATION);
    DuplicateException DUPLICATE_PRODUCT = new DuplicateException(ProductErrorCode.DUPLICATE_PRODUCT);
}
