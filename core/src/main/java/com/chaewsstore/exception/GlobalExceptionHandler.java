package com.chaewsstore.exception;

import com.chaewsstore.util.ResponseCode;
import com.chaewsstore.util.ResponseData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    protected ResponseData handleDuplicateException(DuplicateException e) {
        return ResponseData.of(e.getResponseCode());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseData handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> allFieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> data = new HashMap<>();
        for (FieldError fieldError : allFieldErrors) {
            data.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseData.of(ResponseCode.VALID_ERROR, data);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception e) {
        return e.getMessage();
    }

}
