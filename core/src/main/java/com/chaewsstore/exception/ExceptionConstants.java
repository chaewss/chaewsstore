package com.chaewsstore.exception;

import com.chaewsstore.util.ResponseCode;

public interface ExceptionConstants {

    DuplicateException ACCOUNT_DUPLICATION = new DuplicateException(ResponseCode.ACCOUNT_DUPLICATION);
    DuplicateException NICKNAME_DUPLICATION = new DuplicateException(ResponseCode.NICKNAME_DUPLICATION);

}
