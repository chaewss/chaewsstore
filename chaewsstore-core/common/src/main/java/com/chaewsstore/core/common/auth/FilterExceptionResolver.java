package com.chaewsstore.core.common.auth;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FilterExceptionResolver<T extends RuntimeException> {

    void setResponse(HttpServletResponse response, T ex) throws IOException;
}
