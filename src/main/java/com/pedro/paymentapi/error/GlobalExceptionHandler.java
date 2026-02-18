package com.pedro.paymentapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public com.pedro.paymentapi.payment.error.ApiError notFound(NotFoundException ex, HttpServletRequest request) {
        return new com.pedro.paymentapi.payment.error.ApiError(
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public com.pedro.paymentapi.payment.error.ApiError validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        return new com.pedro.paymentapi.payment.error.ApiError(
                400,
                "Request validation error",
                fieldErrors,
                request.getRequestURI()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public com.pedro.paymentapi.payment.error.ApiError generic(Exception ex, HttpServletRequest request) {
        return new com.pedro.paymentapi.payment.error.ApiError(
                500,
                "Internal Server Error",
                "Unexpected error",
                request.getRequestURI()
        );
    }
}
