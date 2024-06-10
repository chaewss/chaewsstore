package com.chaewsstore.core.infra.exception;

import com.globalutils.exception.GlobalErrorException;
import lombok.Getter;

@Getter
public class JwtErrorException extends GlobalErrorException {

    private final JwtResponseCode errorCode;

    public JwtErrorException(JwtResponseCode jwtErrorCode) {
        super(jwtErrorCode);
        this.errorCode = jwtErrorCode;
    }
}
