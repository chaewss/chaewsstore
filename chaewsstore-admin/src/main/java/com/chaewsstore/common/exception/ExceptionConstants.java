package com.chaewsstore.common.exception;

import com.chaewsstore.common.response.ResponseCode;

public interface ExceptionConstants {

    DuplicateException ADMIN_DUPLICATION = new DuplicateException(ResponseCode.ADMIN_DUPLICATION);

}
