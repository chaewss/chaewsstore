package com.chaewsstore.common.security.filter;

import static com.chaewsstore.core.common.util.ResponseCode.INVALID_TOKEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.chaewsstore.core.common.auth.FilterExceptionResolver;
import com.chaewsstore.core.common.util.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter implements
    FilterExceptionResolver<JwtException> {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            setResponse(response, ex);
        }
    }

    @Override
    public void setResponse(HttpServletResponse response, JwtException ex) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            objectMapper.writeValueAsString(ResponseData.of(INVALID_TOKEN, ex.getMessage())));
    }
}
