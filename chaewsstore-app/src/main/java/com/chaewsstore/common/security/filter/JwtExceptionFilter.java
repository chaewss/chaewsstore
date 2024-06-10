package com.chaewsstore.common.security.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.chaewsstore.core.infra.exception.JwtErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalutils.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtErrorException ex) {

            response.setStatus(ex.getErrorCode().getStatusCode().getCode());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                .write(objectMapper.writeValueAsString(ErrorResponse.from(ex.getErrorCode())));
        }
    }
}
