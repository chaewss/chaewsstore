package com.chaewsstore.core.infra.exception;

import com.globalutils.exception.GlobalErrorException;
import lombok.Getter;

@Getter
public class JwtErrorException extends GlobalErrorException {

    private final JwtErrorCode errorCode;

    public JwtErrorException(JwtErrorCode jwtErrorCode) {
        super(jwtErrorCode);
        this.errorCode = jwtErrorCode;
    }
}
